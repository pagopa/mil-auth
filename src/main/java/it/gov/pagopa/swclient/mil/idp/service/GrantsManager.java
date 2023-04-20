package it.gov.pagopa.swclient.mil.idp.service;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.swclient.mil.bean.Errors;
import it.gov.pagopa.swclient.mil.idp.ErrorCode;
import it.gov.pagopa.swclient.mil.idp.dao.ClientEntity;
import it.gov.pagopa.swclient.mil.idp.dao.GrantEntity;
import it.gov.pagopa.swclient.mil.idp.dao.GrantRepository;
import it.gov.pagopa.swclient.mil.idp.dao.ResourceOwnerCredentialsEntity;
import it.gov.pagopa.swclient.mil.idp.utils.PasswordVerifier;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class GrantsManager {

    @Inject
    GrantRepository grantRepository;
    /**
     * Verify channel consistency.
     *
     * @param clientEntity
     * @param channel
     * @throws NotAuthorizedException
     */
    public void verifyChannel(ClientEntity clientEntity, String channel) throws NotAuthorizedException {
        if (clientEntity.getChannel().equals(channel)) {
            Log.debug("Channel is consistent.");
        } else {
            Log.warnf("[%s] Inconsistent channel. Expected %s, found %s.", ErrorCode.INCONSISTENT_CHANNEL, channel,
                    clientEntity.getChannel());
            throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new Errors(List.of(ErrorCode.INCONSISTENT_CHANNEL))).build());
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
    public void verifyCredentials(ResourceOwnerCredentialsEntity credentialsEntity, String acquirerId, String channel,
                                   String merchantId, String password) throws NotAuthorizedException {
        /*
         * Verify acquirer/channel/merchant consistency.
         */
        if (credentialsEntity.getAcquirerId().equals(acquirerId) && credentialsEntity.getChannel().equals(channel)
                && ((credentialsEntity.getMerchantId() == null && merchantId == null)
                || credentialsEntity.getMerchantId().equals(merchantId))) {
            Log.debug("Acquirer ID, Channel and Merchant ID are consistent.");
            /*
             * Verify password.
             */
            try {
                if (PasswordVerifier.verify(password, credentialsEntity.getSalt(),
                        credentialsEntity.getPasswordHash())) {
                    Log.debug("Credentials verified successfully");
                } else {
                    /*
                     * Wrong credentials.
                     */
                    Log.warnf("[%s] Wrong credentials.", ErrorCode.WRONG_CREDENTIALS);
                    throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED)
                            .entity(new Errors(List.of(ErrorCode.WRONG_CREDENTIALS))).build());
                }
            } catch (NoSuchAlgorithmException e) {
                /*
                 * Error during credentials verification.
                 */
                Log.errorf(e, "[%s] Error while credentials verification.",
                        ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION);
                throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new Errors(List.of(ErrorCode.ERROR_WHILE_CREDENTIALS_VERIFICATION))).build());
            }
        } else {
            /*
             * Consistentcy check failed.
             */
            Log.warnf("[%s] Acquirer ID, Channel and Merchant ID aren't consistent. Expected %s/%s/%s, found %s/%s/%s.",
                    ErrorCode.CREDENTIALS_INCONSISTENCY, credentialsEntity.getAcquirerId(),
                    credentialsEntity.getChannel(), credentialsEntity.getMerchantId(), acquirerId, channel, merchantId);
            throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new Errors(List.of(ErrorCode.CREDENTIALS_INCONSISTENCY))).build());
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
    private Uni<Optional<GrantEntity>> findGrants(String acquirerId, String channel, String merchantId, String clientId,
                                                  String terminalId) {
        Log.debugf("Find grants for: %s, %s, %s, %s, %s", acquirerId, channel, merchantId, clientId, terminalId);
        Document doc = new Document(Map.of("acquirerId", acquirerId, "channel", channel, "merchantId", merchantId,
                "clientId", clientId, "terminalId", terminalId));
        return grantRepository.findSingleResultOptional(doc);
    }

    /**
     * @param acquirerId
     * @param channel
     * @param merchantId
     * @param clientId
     * @param terminalId
     * @return
     */
    public Uni<Optional<GrantEntity>> processGrants(String acquirerId, String channel, String merchantId,
                                                     String clientId, String terminalId) {
        return findGrants(acquirerId, channel, merchantId, clientId, terminalId).chain(o -> {
            /*
             * If there are no grants for acquirer/channel/merchant/client/terminal, look
             * for grants that are valid for acquirer/channel/merchant/client.
             */
            if (o.isPresent()) {
                return Uni.createFrom().item(o);
            } else {
                return findGrants(acquirerId, channel, merchantId, clientId, "*");
            }
        }).chain(o -> {
            /*
             * If there are no grants for acquirer/channel/merchant/client, look for grants
             * that are valid for acquirer/channel/merchant.
             */
            if (o.isPresent()) {
                return Uni.createFrom().item(o);
            } else {
                return findGrants(acquirerId, channel, merchantId, "*", "*");
            }
        }).chain(o -> {
            /*
             * If there are no grants for acquirer/channel/merchant, look for grants that
             * are valid for acquirer/channel.
             */
            if (o.isPresent()) {
                return Uni.createFrom().item(o);
            } else {
                return findGrants(acquirerId, channel, "*", "*", "*");
            }
        }).chain(o -> {
            /*
             * If there are no grants for acquirer/channel, look for grants that are valid
             * for acquirer.
             */
            if (o.isPresent()) {
                return Uni.createFrom().item(o);
            } else {
                return findGrants(acquirerId, "*", "*", "*", "*");
            }
        }).chain(o -> {
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
}
