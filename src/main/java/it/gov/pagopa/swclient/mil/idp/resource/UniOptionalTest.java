/*
 * UniOptionalTest.java
 *
 * 17 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.resource;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniOnFailure;
import io.smallrye.mutiny.operators.uni.UniOnFailureTransform;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.bean.KeyType;
import it.gov.pagopa.swclient.mil.idp.dao.ClientEntity;
import it.gov.pagopa.swclient.mil.idp.dao.ClientRepository;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsEntity;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsRepository;
import it.gov.pagopa.swclient.mil.idp.utils.PasswordVerifier;

/**
 * @author antonio.tarricone
 *
 */
public class UniOptionalTest {
	/**
	 * Verify channel consistency.
	 * 
	 * @param c
	 * @param channel
	 * @throws NotAuthorizedException
	 */
	private static void verifyChannel(ClientEntity c, String channel) throws NotAuthorizedException {
		if (c.getChannel().equals(channel)) {
			/*
			 * Channel is consistent.
			 */
			System.out.printf("Channel is consistent.%n");
		} else {
			/*
			 * Channel isn't consistent.
			 */
			System.out.printf("[%s] Inconsistent channel. Expected %s, found %s.%n", ErrorCode.INCONSISTENT_CHANNEL, channel, c.getChannel());
			throw new NotAuthorizedException(Response
				.status(Status.UNAUTHORIZED)
				.entity(new Errors(List.of(ErrorCode.INCONSISTENT_CHANNEL)))
				.build());
		}
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
	private static void verifyCredentials(ResourceOwnerCredentialsEntity c, String acquirerId, String channel, String merchantId, String password) throws NotAuthorizedException {
		if (c.getAcquirerId().equals(acquirerId) && c.getChannel().equals(channel) && ((c.getMerchantId() == null && merchantId == null) || c.getMerchantId().equals(merchantId))) {
			System.out.printf("Acquirer/Channel/Merchant are consistent.%n");
			/*
			 * Verify password.
			 */
			try {
				if (PasswordVerifier.verify(password, c.getSalt(), c.getPasswordHash())) {
					/*
					 * Credentials verified successfully.
					 */
					System.out.println("Credentials verified successfully.");
				} else {
					/*
					 * Wrong credentials.
					 */
					System.out.printf("[%s] Wrong credentials.%n", ErrorCode.WRONG_CREDENTIALS);
					throw new NotAuthorizedException(Response
						.status(Status.UNAUTHORIZED)
						.entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS)))
						.build());
				}
			} catch (NoSuchAlgorithmException e) {
				/*
				 * Error during credentials verification.
				 */
				System.out.printf(/* e, */ "[%s] Error while credentials verification.%n", ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION);
				throw new InternalServerErrorException(Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION)))
					.build());
			}
		} else {
			/*
			 * Consistentcy check failed.
			 */
			System.out.printf("[%s] Acquirer/Channel/Merchant aren't  consistent.%n", ErrorCode.CREDENTIALS_INCONSISTENCY);
			throw new NotAuthorizedException(Response
				.status(Status.UNAUTHORIZED)
				.entity(new Errors(List.of(ErrorCode.CREDENTIALS_INCONSISTENCY)))
				.build());
		}
	}

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(KeyType.RSA.name());
		System.out.println(KeyType.RSA.toString());
		
		String clientId = "5254f087-1214-45cd-94ae-fda53c835197";
		String channel = "POS";
		String username = "antonio.tarricone";
		String password = "antonio";

		ClientRepository clientRepository = new ClientRepository();
		ResourceOwnerCredentialsRepository resourceOwnerCredentialsRepository = new ResourceOwnerCredentialsRepository();

		/*
		 * 1) Client id verification.
		 */
		System.out.printf("Finding client.%n");
		clientRepository.findByIdOptional(clientId)
			.onFailure() // Error while finding client id.
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding client id.%n", ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID);
				return new InternalServerErrorException(Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_CLIENT_ID)))
					.build());
			})
			.onItem()
			.transform(o -> o.orElseThrow(() -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block) throw
				 * NotAuthorizedException.
				 */
				System.out.printf("[%s] Client id not found: %s.%n", ErrorCode.CLIENT_ID_NOT_FOUND, clientId);
				return new NotAuthorizedException(Response
					.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.CLIENT_ID_NOT_FOUND)))
					.build());
			}))
			.onItem()
			.invoke(c -> {
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
			.onFailure(t -> !(t instanceof NotAuthorizedException) && !(t instanceof InternalServerErrorException)) // Error while finding credentials (and the fail isn't from ClientEntity stream).
			.transform(t -> {
				System.out.printf(/* t, */ "[%s] Error while finding credentials.%n", ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS);
				return new InternalServerErrorException(Response
					.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new Errors(List.of(ErrorCode.ERROR_WHILE_FINDING_CREDENTIALS)))
					.build());
			})
			.onItem()
			.transform(o -> o.orElseThrow(() -> {
				/*
				 * If the 'optional' item is present return it, otherwise (this is done by this block) throw
				 * NotAuthorizedException.
				 */
				System.out.printf("[%s] Credentials not found: %s.%n", ErrorCode.WRONG_CREDENTIALS, clientId);
				return new NotAuthorizedException(Response
					.status(Status.UNAUTHORIZED)
					.entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS)))
					.build());
			}))
			.onItem()
			.invoke(c -> {
				/*
				 * Verify credentials.
				 */
				verifyCredentials(c, clientId, channel, username, password);
			})
			/*
			 * 3) Grants retrieving.
			 */
			// HERE PUT THE CODE TO VERIFY OTHER STEPS OF AUTH BY MEANS OF '.onItem()'
			// USE uni.then(() -> uni2) BECAUSE I DON'T NEED THE RESULT OF PREVIOUS UNI
			.subscribe()
			.with(item -> System.out.printf("item = %s%n", item), err -> System.out.printf("item = %s%n", err));
	}
}
