/*
 * AzureRolesRepository.java
 * 
 * 3 apr 2024
 */
package it.pagopa.swclient.mil.auth.azure.service;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import it.pagopa.swclient.mil.auth.bean.Roles;
import it.pagopa.swclient.mil.auth.service.role.RolesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class AzureRolesRepository implements RolesRepository {
	/*
	 * 
	 */
	private AzureBlobRepository repository;
	
	/**
	 * Constructor.
	 * 
	 * @param repository
	 */
	@Inject
	AzureRolesRepository(AzureBlobRepository repository) {
		this.repository = repository;
	}

	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getMilRoles(String)
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getMilRoles(@CacheKey String microservice) {
		String fileName = String.format("roles/mil_services/%s.json", microservice);
		return repository.getFile(fileName, Roles.class);
	}
	
	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getServerRoles(String)
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getServerRoles(@CacheKey String subject) {
		String fileName = String.format("roles/servers/%s.json", subject);
		return repository.getFile(fileName, Roles.class);
	}
	
	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getPublicAdministrationRoles(String)
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getPublicAdministrationRoles(@CacheKey String payeeCode) {
		String fileName = String.format("roles/public_administrations/%s.json", payeeCode);
		return repository.getFile(fileName, Roles.class);
	}
	
	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getPosServiceProviderRoles(String)
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getPosServiceProviderRoles(@CacheKey String subject) {
		String fileName = String.format("roles/pos_service_providers/%s.json", subject);
		return repository.getFile(fileName, Roles.class);
	}
	
	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getAtmRoles(String, String)
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getAtmRoles(@CacheKey String bankId, @CacheKey String terminalId) {
		String fileName = String.format("roles/atms/%s/%s/roles.json", bankId, terminalId);
		return repository.getFile(fileName, Roles.class);
	}
	
	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getBankAtmRoles(String)
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getBankAtmRoles(@CacheKey String bankId) {
		String fileName = String.format("roles/atms/%s/roles.json", bankId);
		return repository.getFile(fileName, Roles.class);
	}
	
	/**
	 * @see it.pagopa.swclient.mil.auth.service.role.RolesRepository#getDefaultAtmRoles()
	 */
	@Override
	@CacheResult(cacheName = "roles-cache")
	public Uni<Roles> getDefaultAtmRoles() {
		return repository.getFile("roles/atms/roles.json", Roles.class);
	}
}