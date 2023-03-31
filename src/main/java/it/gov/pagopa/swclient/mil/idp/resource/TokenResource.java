/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

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
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.CommonHeader;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.bean.AccessToken;
import it.gov.pagopa.swclient.mil.idp.bean.GetAccessToken;
import it.gov.pagopa.swclient.mil.idp.dao.ClientEntity;
import it.gov.pagopa.swclient.mil.idp.dao.ClientRepository;
import it.gov.pagopa.swclient.mil.idp.dao.GrantEntity;
import it.gov.pagopa.swclient.mil.idp.dao.GrantRepository;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsEntity;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsRepository;
import it.gov.pagopa.swclient.mil.idp.utils.PasswordVerifier;

/**
 * 
 * @author Antonio Tarricone
 */

/*
 * Aggiungere claim nel refresh token: AcquirerId, Channel, ClientId, MerchantId, TerminalId
 * 
 */
@Path("/pwd/token")
public class TokenResource {
	List<String> payloadAudience = Arrays.asList(
		"https://mil-d-apim.azure-api.net/mil-payment-notice”, “https://mil-d-apim.azure-api.net/mil-fee-calculator");
	String payloadIssuer = "https://mil-d-apim.azure-api.net/mil-idp";
	String refreshIssuer = "https://mil-d-apim.azure-api.net/mil-idp";
	List<String> refreshAudience = Arrays.asList("https://mil-d-apim.azure-api.net/mil-idp");
	Date expirationTime = new Date(2024, 1, 1); // Deprecated, for testing purposes.
	RSAKey rsaJwk;

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
								if (!tokenObject.getIssuer().equals(refreshIssuer)) {
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

	private Uni<AccessToken> generateAccessToken(Date issueTime, Date expirationTime, GrantEntity Grants,
		GetAccessToken getAccessToken, CommonHeader commonHeader) throws JOSEException {
		return Uni.createFrom().item(Grants)
			.onItem()
			.transform(grantEntity -> {
				try {
					rsaJwk = new RSAKeyGenerator(4096).keyUse(KeyUse.SIGNATURE).keyID(UUID.randomUUID().toString())
						.issueTime(issueTime).expirationTime(expirationTime).generate();
				} catch (JOSEException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				}
				
				StringBuffer buffer = new StringBuffer();
				grantEntity.getGrants().stream().forEach(x -> { buffer.append(x); buffer.append(" "); });
				String grants = buffer.toString().trim();
				
				return Uni.createFrom().item(grants);
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				System.out.printf( t.getMessage(),  "[%s] Error while finding grants at try 1.%n",
					ErrorCode.ERROR_GENERATING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_GENERATING_TOKEN))).build());
			})
			.chain(grantsString -> {
				Log.info("Found grants");
				Log.info(grantsString);

				JWTClaimsSet payload = new JWTClaimsSet.Builder().issuer(payloadIssuer).audience(payloadAudience)
					.expirationTime(new Date(new Date().getTime() + 60 * 1000)).claim("scope", grantsString).build();
				JWTClaimsSet refreshPayload = new JWTClaimsSet.Builder().issuer(refreshIssuer).audience(refreshAudience)
					.claim("clientId", getAccessToken.getClientId()).claim("acquirerId", commonHeader.getAcquirerId())
					.claim("channel", commonHeader.getChannel()).claim("merchantId", commonHeader.getMerchantId())
					.expirationTime(new Date(new Date().getTime() + 60 * 60 * 1000)).claim("scope", "offline_access")
					.build();
				JOSEObjectType jo = new JOSEObjectType("at+jwt");
				JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, jo, null, null, null, null, null, null, null, null,
					rsaJwk.getKeyID(), true, null, null);
				SignedJWT accessToken = new SignedJWT(header, payload);

				try {
					JWSSigner signer = new RSASSASigner(rsaJwk);

					accessToken.sign(signer);
					String access = accessToken.serialize();
					AccessToken token = new AccessToken(access, 3600);
					if ((getAccessToken.getScope() != null && !getAccessToken.getScope().isEmpty())&&getAccessToken.getScope().equals("offline_access")) {
						SignedJWT refreshToken = new SignedJWT(header, refreshPayload);
						refreshToken.sign(signer);
						String refresh = refreshToken.serialize();
						System.out.println("Generated refresh token");

						token.setRefreshToken(refresh);

					}
					return Uni.createFrom().item(token);
				} catch (JOSEException e) {
					// TODO Auto-generated catch block
					return Uni.createFrom().failure(e);
				}
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				System.out.printf( t.getMessage(),  "[%s] Error while finding grants at try 2.%n",
					ErrorCode.ERROR_GENERATING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_GENERATING_TOKEN))).build());
			});
	}

	private Uni<AccessToken> generateRefreshToken(Date issueTime, Date expirationTime, Optional<GrantEntity> Grants)
		throws JOSEException {

		return Uni.createFrom().item(Grants)
			.onItem()
			.transform(grantOptional -> {
				GrantEntity gr = grantOptional.orElse(null);
				return Uni.createFrom().item(StringUtils.join(gr.getGrants(), " "));
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException))
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 3.%n",
					ErrorCode.ERROR_GENERATING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_GENERATING_TOKEN))).build());
			})
			.chain(grantsString -> {
				JWTClaimsSet payload = new JWTClaimsSet.Builder().issuer(payloadIssuer).audience(payloadAudience)
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

	private void verifyChannel(ClientEntity c, String channel) throws NotAuthorizedException {
		Uni.createFrom().item(c)
			.onItem()
			.transform(o -> {
				if (c.getChannel().equals(channel)) {
					System.out.printf("Channel is consistent.%n");
				} else {
					System.out.printf("[%s] Inconsistent channel. Expected %s, found %s.%n", ErrorCode.INCONSISTENT_CHANNEL,
						channel, c.getChannel());
					throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(ErrorCode.INCONSISTENT_CHANNEL))).build());
				}
				return c;
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
	 * Verify crendentials.
	 * 
	 * @param c
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param password
	 * @throws NotAuthorizedException
	 */
	private void verifyCredentials(ResourceOwnerCredentialsEntity c, String acquirerId, String client, String channel,
		String merchantId, String password) throws NotAuthorizedException {
		Uni.createFrom().item(c).onItem()
			.transform(resOwnCredential -> {
				if (resOwnCredential.getAcquirerId().equals(acquirerId) && resOwnCredential.getChannel().equals(channel)
					&& ((resOwnCredential.getMerchantId() == null && merchantId == null) || resOwnCredential.getMerchantId().equals(merchantId))) {
					System.out.printf("Acquirer/Channel/Merchant are consistent.%n");
					/*
					 * Verify password.
					 */
					try {
						if (PasswordVerifier.verify(password, resOwnCredential.getSalt(), resOwnCredential.getPasswordHash())) {
							System.out.printf("Credential verified successfully (test)");
						} else {
							/*
							 * Wrong credentials.
							 */
							System.out.printf("[%s] Wrong credentials.%n", ErrorCode.WRONG_CREDENTIALS);
							throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
								.entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS))).build());
						}
					} catch (NoSuchAlgorithmException e) {
						/*
						 * Error during credentials verification.
						 */
						System.out.printf(/* e, */ "[%s] Error while credentials verification.%n",
							ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION);
						throw new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION))).build());
					}
				} else {
					/*
					 * Consistentcy check failed.
					 */
					System.out.printf(resOwnCredential.getAcquirerId() + " " + acquirerId);

					System.out.printf("[%s] Acquirer/Channel/Merchant aren't  consistent.%n",
						ErrorCode.CREDENTIALS_INCONSISTENCY);
					throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(ErrorCode.CREDENTIALS_INCONSISTENCY))).build());
				}
				return null;
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) // Error
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 6.%n",
					ErrorCode.ERROR_CHECKING_TOKEN);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_CHECKING_TOKEN))).build());
			});

	}
	
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
	 * 
	 * @param commonHeader
	 * @param getAccessToken
	 * @return
	 */
	private Uni<AccessToken> createToken(CommonHeader commonHeader, GetAccessToken getAccessToken) {

		System.out.printf("createToken - Input parameters: %s, %s", commonHeader, getAccessToken);

		ClientRepository clientRepository = new ClientRepository();
		ResourceOwnerCredentialsRepository resourceOwnerCredentialsRepository = new ResourceOwnerCredentialsRepository();
		GrantRepository grantRepository = new GrantRepository();

		String clientId = getAccessToken.getClientId();
		String channel = commonHeader.getChannel();
		String username = getAccessToken.getUsername();
		String password = getAccessToken.getPassword();
		String merchantId = commonHeader.getMerchantId();
		String acquirerId = commonHeader.getAcquirerId();
		String terminalId = commonHeader.getTerminalId();

		System.out.printf("Finding client.%n");
		Uni<AccessToken> AccessTokenUni = clientRepository.findByIdOptional(clientId).onFailure() // Error while finding
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
				System.out.printf("[%s] Client id not found %s.%n", ErrorCode.CLIENT_ID_NOT_FOUND, clientId);
				return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.CLIENT_ID_NOT_FOUND))).build());
			}))
			.onFailure() // Error
.transform(t -> {
System.out.printf(/* t, */ "[%s] Client id not found.%n",
ErrorCode.CLIENT_ID_NOT_FOUND);
return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
.entity(new Errors(List.of(ErrorCode.CLIENT_ID_NOT_FOUND))).build());
}).
onItem().
			invoke(c -> {
				/*
				 * Verify channel consistency.
				 */
				verifyChannel(c, channel);
			})
			/*
			 * 2) Credential verification.
			 */
			.chain(() -> {
				/*
				 * Retrieve credentials.
				 */
				System.out.printf("Finding credentials.%n");
				return resourceOwnerCredentialsRepository.findByIdOptional(username);
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
				System.out.printf(/* t, */ "[%s] Error while finding credentials.%n",
					ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS))).build());
			}).onItem().transform(o -> o.orElseThrow(() -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block) throw
				 * NotAuthorizedException.
				 */
				System.out.printf("[%s] Credentials not found: %s.%n", ErrorCode.WRONG_CREDENTIALS, clientId);
				return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS))).build());
			})).onItem().invoke(c -> {
				/*
				 * Verify credentials.
				 */
				verifyCredentials(c, acquirerId, clientId, channel, merchantId, password);
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
					Log.info("Last try finding grants");
					return grantRepository.findSingleResultOptional(d);
				}
			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) 
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding grants at try 7.%n",
					ErrorCode.ERROR_WHILE_FINDING_GRANTS);
				return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_GRANTS))).build());
			})
			.onItem()
			.transform(o -> o.orElseThrow(()->{
				System.out.printf("[%s] Grants not found: %s.%n", ErrorCode.WRONG_CREDENTIALS, clientId);
				return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS))).build());
			}))
			.chain((grantEntity) -> {
					Log.debug("Grants not null!");
				Uni<AccessToken> token;
				try {
					token = generateAccessToken(new Date(), new Date(), grantEntity, getAccessToken,
						commonHeader);
					return token;
				} catch (JOSEException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Uni.createFrom().failure(e);
				}

			})
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) 
			.transform(t -> {
				
					System.out.printf(t.getMessage(),"No grants found",
							ErrorCode.ERROR_WHILE_FINDING_GRANTS);
						return new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
							.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_GRANTS))).build());
				
				
			});
			
			

		return AccessTokenUni;
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
	
	@GET
	public String test() {
		return "Ciao";
	}
}