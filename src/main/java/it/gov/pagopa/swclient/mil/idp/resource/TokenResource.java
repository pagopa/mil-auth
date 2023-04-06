/*
 * TokenResource.java
 *
 * 16 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
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
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
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
import it.gov.pagopa.swclient.mil.idp.bean.PublicKey;
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
	
	@Inject
	TokenStringGenerator tokenStringGenerator;
	
	@Inject
	RefreshTokenStringGenerator refreshTokenStringGenerator;

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
	 * @param acquirerId
	 * @param channel
	 * @param merchantId
	 * @param clientId
	 * @param terminalId
	 * @return
	 */
	private Uni<Optional<GrantEntity>> processGrants(String acquirerId, String channel, String merchantId, String clientId, String terminalId) {
		return findGrants(acquirerId, channel, merchantId, clientId, terminalId)
			.chain(o -> {
				/*
				 * If there are no grants for acquirer/channel/merchant/client/terminal, look for grants that are
				 * valid for acquirer/channel/merchant/client.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, channel, merchantId, clientId, "*");
				}
			})
			.chain(o -> {
				/*
				 * If there are no grants for acquirer/channel/merchant/client, look for grants that are valid for
				 * acquirer/channel/merchant.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, channel, merchantId, "*", "*");
				}
			})
			.chain(o -> {
				/*
				 * If there are no grants for acquirer/channel/merchant, look for grants that are valid for
				 * acquirer/channel.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, channel, "*", "*", "*");
				}
			})
			.chain(o -> {
				/*
				 * If there are no grants for acquirer/channel, look for grants that are valid for acquirer.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants(acquirerId, "*", "*", "*", "*");
				}
			})
			.chain(o -> {
				/*
				 * If there are no grants for acquirer, look for grants that are valid for all.
				 */
				if (o.isPresent()) {
					return Uni.createFrom().item(o);
				} else {
					return findGrants("*", "*", "*", "*", "*");
				}
			});
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
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private PrivateKey getPrivateKey(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger modulus = Base64URL.from(keyPair.getN()).decodeToBigInteger();
		BigInteger privateExponent = Base64URL.from(keyPair.getD()).decodeToBigInteger();
		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(spec);
	}


	/**
	 * 
	 * @param keyPair
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private RSAPublicKey getPublicKey(PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger modulus = Base64URL.from(publicKey.getN()).decodeToBigInteger();
		BigInteger exponent = Base64URL.from(publicKey.getE()).decodeToBigInteger();
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) (factory.generatePublic(spec));
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
		return keyRetriever.getKeyPair()
			.chain(key -> {
				JWSSigner signer;
				try {
					signer = new RSASSASigner(getPrivateKey(key));
				Date now = new Date();
				
				JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, null, null, null, null, null, null, null, null, null, key.getKid(), true, null, null);
				SignedJWT accessToken = tokenStringGenerator.generateAccessToken(header, key, getAccessToken, commonHeader, grantEntity, signer);
				String refreshTokenStr = refreshTokenStringGenerator.generateRefreshTokenString(commonHeader, getAccessToken, header, signer);
				
				AccessToken token = new AccessToken(accessToken.serialize(), refreshTokenStr, accessDuration);
				Log.debug("Tokens generated successfully.");
				return Uni.createFrom().item(token);
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Uni.createFrom().failure(e);
				}
			});
	}

	/**
	 * 
	 * @param commonHeader
	 * @param getAccessToken
	 * @return
	 */
	private Uni<AccessToken> commonProcessing(CommonHeader commonHeader, GetAccessToken getAccessToken) {
		String clientId = getAccessToken.getClientId();
		String acquirerId = commonHeader.getAcquirerId();
		String channel = commonHeader.getChannel();
		String merchantId = commonHeader.getMerchantId();
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
			.onItem()
			.transform(o -> o.orElseThrow(() -> {
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
			.onItem()
			.invoke(c -> {
				/*
				 * Verify channel consistency.
				 */
				verifyChannel(c, channel);
			})
			.chain(() -> {
				/*
				 * Find grants.
				 */
				return processGrants(acquirerId, channel, merchantId, clientId, terminalId);
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
	 * @param refreshToken
	 * @throws NotAuthorizedException
	 */
	private void verifyAlgorithm(SignedJWT refreshToken) throws NotAuthorizedException {
		Log.debug("Verify refresh token algorithm.");
		if (!Objects.equals(refreshToken.getHeader().getAlgorithm(), JWSAlgorithm.RS256)) {
			Log.warnf("[%s] Wrong refresh token algorithm. Expected %s, found %s.", ErrorCode.WRONG_REFRESH_TOKEN_ALGORITHM, JWSAlgorithm.RS256, refreshToken.getHeader().getAlgorithm());
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.WRONG_REFRESH_TOKEN_ALGORITHM)))
					.build());
		}
	}

	/**
	 * 
	 * @param refreshTokenClaimsSet
	 * @throws NotAuthorizedException
	 */
	private void verifyIssuer(JWTClaimsSet refreshTokenClaimsSet) throws NotAuthorizedException {
		Log.debug("Verify refresh token issuer.");
		String currentIssuer = refreshTokenClaimsSet.getIssuer();
		if (!Objects.equals(currentIssuer, issuer)) {
			Log.warnf("[%s] Wrong refresh token issuer. Expected %s, found %s.", ErrorCode.WRONG_REFRESH_TOKEN_ISSUER, issuer, currentIssuer);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.WRONG_REFRESH_TOKEN_ISSUER)))
					.build());
		}
	}

	/**
	 * 
	 * @param refreshTokenClaimsSet
	 * @throws NotAuthorizedException
	 */
	private void verifyIssueTime(JWTClaimsSet refreshTokenClaimsSet) throws NotAuthorizedException {
		Log.debug("Verify refresh token issue time.");
		long threshold = new Date().getTime();
		Date issueTime = refreshTokenClaimsSet.getIssueTime();
		if (issueTime == null || issueTime.getTime() > threshold) {
			Log.warnf("[%s] Wrong issue time. Found %d but threshold is %d.", ErrorCode.WRONG_REFRESH_TOKEN_ISSUE_TIME, issueTime.getTime(), threshold);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.WRONG_REFRESH_TOKEN_ISSUE_TIME)))
					.build());
		}
	}

	/**
	 * 
	 * @param refreshTokenClaimsSet
	 * @throws NotAuthorizedException
	 */
	private void verifyExpirationTime(JWTClaimsSet refreshTokenClaimsSet) throws NotAuthorizedException {
		Log.debug("Verify refresh token expiration time.");
		Date expirationTime = refreshTokenClaimsSet.getExpirationTime();
		if (expirationTime == null || expirationTime.getTime() < LocalDate.now().toEpochDay()) {
			Log.warnf("[%s] Refresh token expired.", ErrorCode.REFRESH_TOKEN_EXPIRED);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.REFRESH_TOKEN_EXPIRED)))
					.build());
		}
	}

	/**
	 * 
	 * @param refreshTokenClaimsSet
	 * @throws NotAuthorizedException
	 */
	private void verifyAudience(JWTClaimsSet refreshTokenClaimsSet) throws NotAuthorizedException {
		Log.debug("Verify refresh token audience.");

		String refreshAudienceStr = Arrays.toString(refreshAudience
			.stream()
			.sorted()
			.collect(Collectors.toList())
			.toArray());

		List<String> currentRefreshAudience = refreshTokenClaimsSet.getAudience();
		if (refreshAudience == null) {
			Log.warnf("[%s] Wrong refresh token audience. Expected %s, found %s.", ErrorCode.WRONG_REFRESH_TOKEN_AUDIENCE, refreshAudienceStr, refreshAudience);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.WRONG_REFRESH_TOKEN_AUDIENCE)))
					.build());
		}

		String currentRefreshAudienceStr = Arrays.toString(currentRefreshAudience
			.stream()
			.sorted()
			.collect(Collectors.toList())
			.toArray());

		if (!currentRefreshAudienceStr.equals(refreshAudienceStr)) {
			Log.warnf("[%s] Wrong refresh token audience. Expected %s, found %s.", ErrorCode.WRONG_REFRESH_TOKEN_AUDIENCE, refreshAudienceStr, currentRefreshAudienceStr);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.WRONG_REFRESH_TOKEN_AUDIENCE)))
					.build());
		}
	}

	/**
	 * 
	 * @param refreshTokenClaimsSet
	 * @throws NotAuthorizedException
	 */
	private void verifyScope(JWTClaimsSet refreshTokenClaimsSet) throws NotAuthorizedException {
		Log.debug("Verify refresh token scope.");
		Object scope = refreshTokenClaimsSet.getClaim("scope");
		if (!Objects.equals(scope, "offline_access")) {
			Log.warnf("[%s] Wrong refresh token scope. Expected %s, found %s.", ErrorCode.WRONG_REFRESH_TOKEN_SCOPE, "offline_access", scope);
			throw new NotAuthorizedException(
				Response.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(
						ErrorCode.WRONG_REFRESH_TOKEN_SCOPE)))
					.build());
		}
	}

	/**
	 * 
	 * @param refreshToken
	 * @return
	 */
	private Uni<Void> verifySignature(SignedJWT refreshToken) {
		return keyRetriever.getPublicKey(refreshToken.getHeader().getKeyID())
			.onItem()
			.transform(key -> key.orElseThrow(() -> {
				Log.warnf("[%s] Key %s not found.", ErrorCode.KEY_NOT_FOUND, refreshToken.getHeader().getKeyID());
				return new NotAuthorizedException(
					Response.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(
							ErrorCode.KEY_NOT_FOUND)))
						.build());
			}))
			.chain(key -> {
				try {
					JWSVerifier verifier = new RSASSAVerifier(getPublicKey(key));
					boolean signatureOk = refreshToken.verify(verifier);
					if (signatureOk) {
						return Uni.createFrom().voidItem();
					} else {
						return Uni.createFrom().failure(new InternalServerErrorException(
							Response.status(Status.UNAUTHORIZED)
								.entity(new Errors(List.of(
									ErrorCode.WRONG_SIGNATURE)))
								.build()));
					}
				} catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException e) {
					Log.errorf(e, "[%s] Error while signature verification.", ErrorCode.ERROR_WHILE_SIGNATURE_VERIFICATION);
					return Uni.createFrom().failure(new InternalServerErrorException(
						Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(new Errors(List.of(
								ErrorCode.ERROR_WHILE_SIGNATURE_VERIFICATION)))
							.build()));
				}
			});
	}

	/**
	 * 
	 * @param refreshTokenStr
	 * @return
	 */
	private Uni<Void> verifyRefreshToken(String refreshTokenStr) {
		try {
			SignedJWT refreshToken = SignedJWT.parse(refreshTokenStr);
			JWTClaimsSet refreshTokenClaimsSet = refreshToken.getJWTClaimsSet();
			verifyAlgorithm(refreshToken);
			verifyIssuer(refreshTokenClaimsSet);
			verifyIssueTime(refreshTokenClaimsSet);
			verifyExpirationTime(refreshTokenClaimsSet);
			verifyAudience(refreshTokenClaimsSet);
			verifyScope(refreshTokenClaimsSet);
			return verifySignature(refreshToken);
		} catch (ParseException e) {
			Log.errorf(e, "[%s] Error while parsing token.", ErrorCode.ERROR_PARSING_TOKEN);
			return Uni.createFrom().failure(new InternalServerErrorException(
				Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(
						ErrorCode.ERROR_PARSING_TOKEN)))
					.build()));
		} catch (NotAuthorizedException e) {
			return Uni.createFrom().failure(e);
		}
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

		String channel = commonHeader.getChannel();
		String username = getAccessToken.getUsername();
		String password = getAccessToken.getPassword();
		String merchantId = commonHeader.getMerchantId();
		String acquirerId = commonHeader.getAcquirerId();

		/*
		 * Retrieve credentials.
		 */
		Log.debug("Find credentials.");
		return resourceOwnerCredentialsRepository.findByIdOptional(username)
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
			.onItem()
			.transform(credentials -> credentials.orElseThrow(() -> {
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
			.onItem()
			.invoke(credentials -> {
				/*
				 * Verify credentials.
				 */
				verifyCredentials(credentials, acquirerId, channel, merchantId, password);
			})
			.chain(() -> {
				return commonProcessing(commonHeader, getAccessToken);
			});
	}

	/**
	 * 
	 * @param commonHeader
	 * @param refreshAccessToken
	 * @return
	 */
	private Uni<AccessToken> refreshToken(CommonHeader commonHeader, GetAccessToken refreshAccessToken) {
		Log.debugf("refreshToken - Input parameters: %s, %s", commonHeader, refreshAccessToken);

		String refreshTokenStr = refreshAccessToken.getRefreshToken();

		/*
		 * Retrieve credentials.
		 */
		Log.debug("Find credentials.");
		return verifyRefreshToken(refreshTokenStr)
			.chain(() -> {
				return commonProcessing(commonHeader, refreshAccessToken);
			});
	}
}