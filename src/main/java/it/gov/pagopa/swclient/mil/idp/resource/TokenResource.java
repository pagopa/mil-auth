/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.bean.AccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.KeyPair;
import it.gov.pagopa.swclient.mil.idp.dao.ClientEntity;
import it.gov.pagopa.swclient.mil.idp.dao.ClientRepository;
import it.gov.pagopa.swclient.mil.idp.dao.GrantEntity;
import it.gov.pagopa.swclient.mil.idp.dao.GrantRepository;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsEntity;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsRepository;
import it.gov.pagopa.swclient.mil.idp.service.KeyRetriever;
import it.gov.pagopa.swclient.mil.idp.utils.PasswordVerifier;

/**
 * 
 * @author Antonio Tarricone & Anis Lucidi
 */
@Path("/pwd/token")
public class TokenResource {
	/*
	 * 
	 */
	@ConfigProperty(name = "issuer")
	String issuer;

	/*
	 * 
	 */
	@ConfigProperty(name = "access.audience")
	List<String> accessAudience;

	/*
	 * 
	 */
	@ConfigProperty(name = "refresh.audience")
	List<String> refreshAudience;

	/*
	 * Duration of access tokens in seconds.
	 */
	@ConfigProperty(name = "access.duration")
	long accessDuration;

	/*
	 * Duration of refresh tokens in seconds.
	 */
	@ConfigProperty(name = "refresh.duration")
	long refreshDuration;

	/*
	 * 
	 */
	@Inject
	ClientRepository clientRepository;

	/*
	 * 
	 */
	@Inject
	ResourceOwnerCredentialsRepository resourceOwnerCredentialsRepository;

	/*
	 * 
	 */
	@Inject
	GrantRepository grantRepository;

	/*
	 * 
	 */
	@Inject
	KeyRetriever keyRetriever;

	/**
	 * Verify channel consistency.
	 * 
	 * @param clientEntity
	 * @param channel
	 * @throws NotAuthorizedException
	 */
	private void verifyChannel(ClientEntity clientEntity, String channel) throws NotAuthorizedException {
		if (clientEntity.getChannel().equals(channel)) {
			Log.debug("Channel is consistent.");
		} else {
			Log.warnf("[%s] Inconsistent channel. Expected %s, found %s.", ErrorCode.INCONSISTENT_CHANNEL,
				channel, clientEntity.getChannel());
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.INCONSISTENT_CHANNEL)))
					.build());
		}
	}

	/**
	 * Verify credentials.
	 * 
	 * @param credentialsEntity
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param password
	 * @throws NotAuthorizedException
	 */
	private void verifyCredentials(ResourceOwnerCredentialsEntity credentialsEntity, String acquirerId, String channel, String merchantId, String password) throws NotAuthorizedException {
		/*
		 * Verify acquirer/channel/merchant consistency.
		 */
		if (credentialsEntity.getAcquirerId().equals(acquirerId)
			&& credentialsEntity.getChannel().equals(channel)
			&& ((credentialsEntity.getMerchantId() == null && merchantId == null) || credentialsEntity.getMerchantId().equals(merchantId))) {
			Log.debug("Acquirer ID, Channel and Merchant ID are consistent.");
			/*
			 * Verify password.
			 */
			try {
				if (PasswordVerifier.verify(password, credentialsEntity.getSalt(), credentialsEntity.getPasswordHash())) {
					Log.debug("Credentials verified successfully");
				} else {
					/*
					 * Wrong credentials.
					 */
					Log.warnf("[%s] Wrong credentials.", ErrorCode.WRONG_CREDENTIALS);
					throw new NotAuthorizedException(
						Response.status(Status.UNAUTHORIZED)
							.entity(new Errors(
								List.of(ErrorCode.WRONG_CREDENTIALS)))
							.build());
				}
			} catch (NoSuchAlgorithmException e) {
				/*
				 * Error during credentials verification.
				 */
				Log.errorf(e, "[%s] Error while credentials verification.", ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION);
				throw new InternalServerErrorException(
					Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(
							ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION)))
						.build());
			}
		} else {
			/*
			 * Consistentcy check failed.
			 */
			Log.warnf("[%s] Acquirer ID, Channel and Merchant ID aren't consistent. Expected %s/%s/%s, found %s/%s/%s.",
				ErrorCode.CREDENTIALS_INCONSISTENCY,
				credentialsEntity.getAcquirerId(),
				credentialsEntity.getChannel(),
				credentialsEntity.getMerchantId(),
				acquirerId,
				channel,
				merchantId);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(
						List.of(ErrorCode.CREDENTIALS_INCONSISTENCY)))
					.build());
		}
	}

	/**
	 * Find grants.
	 * 
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	private Uni<Optional<GrantEntity>> findGrants(String acquirerId, String channel, String merchantId, String clientId, String terminalId) {
		Log.debugf("Find grants for: %s, %s, %s, %s, %s", acquirerId, channel, merchantId, clientId, terminalId);
		Document doc = new Document(Map.of(
			"acquirerId", acquirerId,
			"channel", channel,
			"merchantId", merchantId,
			"clientId", clientId,
			"terminalId", terminalId));
		return grantRepository.findSingleResultOptional(doc);
	}

	/**
	 * 
	 * @param strings
	 * @return
	 */
	private String concat(List<String> strings) {
		StringBuffer buffer = new StringBuffer();
		strings.forEach(x -> {
			buffer.append(x);
			buffer.append(" ");
		});
		return buffer.toString().trim();
	}

	/**
	 * 
	 * @param keyPair
	 * @return
	 */
	private PrivateKey getPrivateKey(KeyPair keyPair) {
		BigInteger modulus = Base64URL.from(keyPair.getN()).decodeToBigInteger();
		BigInteger privateExponent = Base64URL.from(keyPair.getD()).decodeToBigInteger();
		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(spec);
	}

	/**
	 * 
	 * @param commonHeader
	 * @param getAccessToken
	 * @param grantEntity
	 * @return
	 */
	private Uni<AccessToken> generateAccessToken(CommonHeader commonHeader, GetAccessToken getAccessToken, GrantEntity grantEntity) {
		Log.debug("Retrieve key pair.");
		keyRetriever.getKeyPair()
			.chain(key -> {
				Date now = new Date();

				JWTClaimsSet accessPayload = new JWTClaimsSet.Builder()
					.issuer(issuer)
					.audience(accessAudience)
					.issueTime(now)
					.expirationTime(new Date(now.getTime() + accessDuration * 1000))
					.claim("scope", concat(grantEntity.getGrants()))
					.build();

				JWTClaimsSet refreshPayload = new JWTClaimsSet.Builder()
					.issuer(issuer)
					.audience(refreshAudience)
					.issueTime(now)
					.expirationTime(new Date(now.getTime() + refreshDuration * 1000))
					.claim("scope", "offline_access")
					.claim("acquirerId", commonHeader.getAcquirerId())
					.claim("channel", commonHeader.getChannel())
					.claim("merchantId", commonHeader.getMerchantId())
					.claim("clientId", getAccessToken.getClientId())
					.build();

				// JOSEObjectType jo = new JOSEObjectType("at+jwt");
				JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, key.getKid(), true, null, null);

				SignedJWT accessToken = new SignedJWT(header, accessPayload);
				SignedJWT refreshToken = new SignedJWT(header, refreshPayload);

				JWSSigner signer = new RSASSASigner(getPrivateKey(key));
				Log.debug("Sign tokens.");

				try {
					accessToken.sign(signer);
					refreshToken.sign(signer);
					String access = accessToken.serialize();
					AccessToken token = new AccessToken(accessToken.serialize(), refreshToken.serialize(), accessDuration);
					Log.debug("Tokens generated successfully.");
					return Uni.createFrom().item(token);
				} catch (JOSEException e) {
					Log.errorf(e, "Error during tokens signing.");
					return Uni.createFrom().failure(e);
				}
			});
	}

	/**
	 * 
	 * @param refreshToken
	 * @param rsaJwk
	 * @return
	 * @throws ParseException
	 * @throws BadJOSEException
	 * @throws JOSEException
	 * @throws MalformedURLException
	 */
	public Uni<Boolean> isRefreshTokenValid(String refreshToken, RSAKey rsaJwk)
		throws ParseException, BadJOSEException, JOSEException, MalformedURLException {

		Uni<Boolean> isValid = Uni.createFrom().item(refreshToken).onItem().transform(token -> {
			try {
				return SignedJWT.parse(token);
			} catch (ParseException e2) {
				throw new RuntimeException(e2);
			}
		}).onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) // Error
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while parsing token.%n",
					ErrorCode.ERROR_PARSING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_PARSING_TOKEN))).build());
			})
			.chain(signedJwt -> {
				try {
					Uni<Boolean> isVerified = Uni.createFrom().item(signedJwt.getJWTClaimsSet())
						.chain(tokenObject -> {
							if (!tokenObject.getAudience().isEmpty()) {
								if (!tokenObject.getAudience().equals(refreshAudience)) {
									System.out.println("Audience doesn't match");
									return Uni.createFrom().item(false);
								}
								System.out.println("Audience matches");

							}
							if (!tokenObject.getIssuer().isEmpty()) {
								if (!tokenObject.getIssuer().equals(issuer)) {
									System.out.println("Issuer doesn't match");
									return Uni.createFrom().item(false);
								}
								System.out.println("Issuer matches");
							}
							if (!tokenObject.getClaim("scope").equals("offline_access")) {
								System.out.println("Scope doesn't match");
								return Uni.createFrom().item(false);
							}
							System.out.println("Scope matches");
							try {
								JWSVerifier verifier = new RSASSAVerifier(rsaJwk);
								if (signedJwt.verify(verifier)) {
									return Uni.createFrom().item(true);
								} else {
									return Uni.createFrom().item(false);
								}
							} catch (JOSEException e) {
								// TODO Auto-generated catch block
								return Uni.createFrom().failure(e);
							}
						});
					return isVerified;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				}
			});
		return isValid;
	}

	private Uni<AccessToken> generateRefreshToken(Date issueTime, Date expirationTime, Optional<GrantEntity> Grants)
		throws JOSEException {

		return Uni.createFrom().item(Grants)
			.onItem()
			.transform(grantOptional -> {
				GrantEntity gr = grantOptional.orElse(null);
				return Uni.createFrom().item(concat(gr.getGrants()));
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 3.%n",
					ErrorCode.ERROR_GENERATING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_GENERATING_TOKEN))).build());
			})
			.chain(grantsString -> {
				JWTClaimsSet payload = new JWTClaimsSet.Builder().issuer(issuer).audience(accessAudience)
					.expirationTime(new Date(new Date().getTime() + 60 * 1000)).claim("scope", grantsString).build();
				JOSEObjectType jo = new JOSEObjectType("at+jwt");
				JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, jo, null, null, null, null, null, null, null, null,
					rsaJwk.getKeyID(), true, null, null);
				SignedJWT accessToken = new SignedJWT(header, payload);

				try {
					JWSSigner signer = new RSASSASigner(rsaJwk);

					accessToken.sign(signer);
					String access = accessToken.serialize();
					AccessToken token = new AccessToken(access, 3600);

					return Uni.createFrom().item(token);
				} catch (JOSEException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				}
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 4 .%n",
					ErrorCode.ERROR_GENERATING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_GENERATING_TOKEN))).build());
			});

	}

	private Uni<AccessToken> generateTokenFromRefresh(CommonHeader commonHeader,
		GetAccessToken refreshAccessToken) {
		ClientRepository clientRepository = new ClientRepository();
		GrantRepository grantRepository = new GrantRepository();

		String clientId = refreshAccessToken.getClientId();
		String channel = commonHeader.getChannel();
		String merchantId = commonHeader.getMerchantId();
		String acquirerId = commonHeader.getAcquirerId();
		String terminalId = commonHeader.getTerminalId();

		Uni<AccessToken> newAccessToken = clientRepository.findByIdOptional(clientId).onFailure() // Error while finding
																									 // client id.
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding client id.%n",
					ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID))).build());
			}).onItem().transform(o -> o.orElseThrow(() -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block) throw
				 * NotAuthorizedException.
				 */
				System.out.printf("[%s] Client id not found: %s.%n", ErrorCode.CLIENT_ID_NOT_FOUND, clientId);
				return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.CLIENT_ID_NOT_FOUND))).build());
			}))
			.onItem().invoke(c -> {
				/*
				 * Verify channel consistency.
				 */
				verifyChannel(c, channel);
			}).chain(() -> {
				System.out.printf("Verifying grants %n");
				Document d = new Document(Map.of("clientId", clientId, "channel", channel, "merchantId", merchantId,
					"acquirerId", acquirerId, "terminalId", terminalId));
				return grantRepository.findSingleResultOptional(d);
			}).chain((o) -> {
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {

					System.out.printf("Verifying grants %n");
					Document d = new Document(Map.of("acquirerId", acquirerId, "channel", channel, "clientId",
						clientId, "merchantId", merchantId, "terminalId", "*"));
					return grantRepository.findSingleResultOptional(d);
				}
			}).chain(o -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block), look for
				 * grants that are valid for all merchantIds, if you can't find anything, look for grants that are
				 * valid for all clientIds etc. if you can't find anything, throw NotAuthorizedException.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					Document d = new Document(Map.of("acquirerId", acquirerId, "channel", channel, "clientId",
						clientId, "merchantId", "*", "terminalId", "*"));
					return grantRepository.findSingleResultOptional(d);
				}
			}).chain(o -> {
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					Document d = new Document(Map.of("acquirerId", acquirerId, "channel", channel, "clientId", "*",
						"merchantId", "*", "terminalId", "*"));
					return grantRepository.findSingleResultOptional(d);
				}
			}).chain(o -> {
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					Document d = new Document(Map.of("acquirerId", acquirerId, "channel", "*", "clientId", "*",
						"merchantId", "*", "terminalId", "*"));
					return grantRepository.findSingleResultOptional(d);
				}
			}).chain(o -> {
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					Document d = new Document(Map.of("acquirerId", "*", "channel", "*", "clientId", "*",
						"merchantId", "*", "terminalId", "*"));
					return grantRepository.findSingleResultOptional(d);
				}
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) // Error
																													 // while
																													 // finding
																													 // credentials
																													 // (and
																													 // the
																													 // fail
																													 // isn't
																													 // from
																													 // ClientEntity
																													 // stream).
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 5.%n",
					ErrorCode.ERROR_WHILE_FINDING_GRANTS);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_GRANTS))).build());
			}).chain(c -> {
				System.out.printf("Generating token...");

				try {
					Uni<AccessToken> token = generateRefreshToken(new Date(), new Date(), c);
					return token;
				} catch (JOSEException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				}
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				System.out.printf("Error while generating refreshed token",
					ErrorCode.ERROR_WHILE_GENERATING_REFRESHED_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_GENERATING_REFRESHED_TOKEN))).build());
			})

		;
		return newAccessToken;
	}

	/**
	 * Dispatches the request to the right method.
	 * 
	 * @param commonHeader
	 * @param getAccessToken
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<AccessToken> createOrRefreshToken(@Valid @BeanParam CommonHeader commonHeader,
		@Valid @BeanParam GetAccessToken getAccessToken) {
		if (getAccessToken.getGrantType().equals("password")) {
			return createToken(commonHeader, getAccessToken);
		} else {
			return refreshToken(commonHeader, getAccessToken);
		}
	}

	/**
	 * Create access and refresh tokens by means of username/password.
	 * 
	 * @param commonHeader
	 * @param getAccessToken
	 * @return
	 */
	private Uni<AccessToken> createToken(CommonHeader commonHeader, GetAccessToken getAccessToken) {
		Log.debugf("createToken - Input parameters: %s, %s", commonHeader, getAccessToken);

		String clientId = getAccessToken.getClientId();
		String channel = commonHeader.getChannel();
		String username = getAccessToken.getUsername();
		String password = getAccessToken.getPassword();
		String merchantId = commonHeader.getMerchantId();
		String acquirerId = commonHeader.getAcquirerId();
		String terminalId = commonHeader.getTerminalId();

		Log.debug("Find client id.");
		return clientRepository.findByIdOptional(clientId)
			.onFailure() // Error while finding client id.
			.transform(t -> {
				Log.errorf(t, "[%s] Error while finding client id.", ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID);
				return new InternalServerErrorException(
					Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(
							ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID)))
						.build());
			})
			.onItem().transform(o -> o.orElseThrow(() -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block) throw
				 * NotAuthorizedException.
				 */
				Log.warnf("[%s] Client id not found: %s", ErrorCode.CLIENT_ID_NOT_FOUND, clientId);
				return new NotAuthorizedException(
					Response.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(
							ErrorCode.CLIENT_ID_NOT_FOUND)))
						.build());
			}))
			.onItem().invoke(c -> {
				/*
				 * Verify channel consistency.
				 */
				verifyChannel(c, channel);
			})
			/*
			 * Credential verification.
			 */
			.chain(() -> {
				/*
				 * Retrieve credentials.
				 */
				Log.debug("Find credentials.");
				return resourceOwnerCredentialsRepository.findByIdOptional(username);
			})
			.onFailure(
				/*
				 * It the failure is not for previous errors, then it must be for an error during credentials
				 * retrieving.
				 */
				t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				Log.errorf(t, "[%s] Error while finding credentials.", ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS);
				return new InternalServerErrorException(
					Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(
							ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS)))
						.build());
			})
			.onItem().transform(o -> o.orElseThrow(() -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block) throw
				 * NotAuthorizedException.
				 */
				Log.warnf("[%s] Credentials not found.", ErrorCode.WRONG_CREDENTIALS);
				return new NotAuthorizedException(
					Response.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(
							ErrorCode.WRONG_CREDENTIALS)))
						.build());
			}))
			.onItem().invoke(c -> {
				/*
				 * Verify credentials.
				 */
				verifyCredentials(c, acquirerId, channel, merchantId, password);
			})
			.chain(() -> {
				/*
				 * Find grants for acquirer/channel/merchant/client/terminal
				 */
				return findGrants(acquirerId, channel, merchantId, clientId, terminalId);
			})
			.chain(o -> {
				/*
				 * If the 'optional' item is present return it, otherwise look for grants that are valid for all
				 * terminals.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, channel, merchantId, clientId, "*");
				}
			})
			.chain(o -> {
				/*
				 * If the 'optional' item is present return it, otherwise look for grants that are valid for all
				 * clients.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, channel, merchantId, "*", "*");
				}
			})
			.chain(o -> {
				/*
				 * If the 'optional' item is present return it, otherwise look for grants that are valid for all
				 * merchants.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, channel, "*", "*", "*");
				}
			})
			.chain(o -> {
				/*
				 * If the 'optional' item is present return it, otherwise look for grants that are valid for all
				 * channels.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, "*", "*", "*", "*");
				}
			})
			.chain(o -> {
				if (o.isPresent()) {
					/*
					 * If the 'optional' item is present return it, otherwise look for grants that are valid for all
					 * acquirers.
					 */
					return Uni.createFrom().item(o);
				} else {
					return findGrants("*", "*", "*", "*", "*");
				}
			})
			.onItem()
			.transform(o -> o.orElseThrow(() -> {
				Log.warnf("[%s] Grants not found.", ErrorCode.GRANTS_NOT_FOUND);
				return new NotAuthorizedException(
					Response.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(
							ErrorCode.GRANTS_NOT_FOUND)))
						.build());
			}))
			.onFailure(
				/*
				 * If an error occurs during retriving grants.
				 */
				t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				Log.errorf(t, "[%s] Error while finding grants.", ErrorCode.ERROR_WHILE_FINDING_GRANTS);
				return new InternalServerErrorException(
					Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(
							ErrorCode.ERROR_WHILE_FINDING_GRANTS)))
						.build());
			})
			.chain((grantEntity) -> {
				/*
				 * Generate tokens.
				 */
				return generateAccessToken(commonHeader, getAccessToken, grantEntity);
			})
			.onFailure(
				/*
				 * Error during token signing.
				 */
				t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				/*
				 * Err
				 */
				return new InternalServerErrorException(
					Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(new Errors(List.of(
							ErrorCode.ERROR_WHILE_SIGNING_TOKENS)))
						.build());

			});
	}

	/**
	 * 
	 * @param commonHeader
	 * @param refreshAccessToken
	 * @return
	 */
	private Uni<AccessToken> refreshToken(CommonHeader commonHeader, GetAccessToken refreshAccessToken) {
		System.out.printf("createToken - Input parameters: %s, %s", commonHeader, refreshAccessToken);

		Uni<AccessToken> newToken = Uni.createFrom().item(refreshAccessToken)
			.onItem()
			.transform(o -> {
				Uni<Boolean> isValid;

				try {
					isValid = isRefreshTokenValid(refreshAccessToken.getRefreshToken(), rsaJwk);
					return isValid;

				} catch (MalformedURLException e) {
					e.printStackTrace();
					return Uni.createFrom().failure(e);
				} catch (ParseException e) {
					// TODO Auto-generated catch block

					return Uni.createFrom().failure(e);
				} catch (BadJOSEException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				} catch (JOSEException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				}
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) // Error

			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 8.%n",
					ErrorCode.ERROR_CHECKING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_CHECKING_TOKEN))).build());
			})
			.chain(o -> {
				System.out.println("Refresh token valid");
				Uni<AccessToken> newAccessToken = generateTokenFromRefresh(commonHeader, refreshAccessToken);

				return newAccessToken;
			});
		return newToken;

	}
}