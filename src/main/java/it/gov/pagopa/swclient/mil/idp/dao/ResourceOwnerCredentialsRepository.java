/*
 * ResourceOwnerCredentialsRepository.java
 *
 * 20 mar 2023
 */
package it.gov.pagopa.swclient.mil.idp.dao;

import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;

/**
 * 
 * @author Antonio Tarricone
 */
@ApplicationScoped
public class ResourceOwnerCredentialsRepository {
	/*
	 * 
	 */
	private static final Map<String, ResourceOwnerCredentialsEntity> REPOSITORY = Map.ofEntries(
		Map.entry("carlo.dechellis", new ResourceOwnerCredentialsEntity("carlo.dechellis", "1uw3ZCkpjKlocU+8BtIT+kvgtAvMYxQduvk9g7TGMPU3tzFtEwodvgZPZ6HsdinW8rH3ldXZK0af3lJngSgPvg==", "gqQojcEKK7CQd1qLIJNmz6XvZKZsyG0rQx3KJCOmAjI=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("alessandro.digregorio", new ResourceOwnerCredentialsEntity("alessandro.digregorio", "fQbg+e0yPvw5Qk+kPe+epkYnrXBBgWha95lsVZKQydDfE07kyQl8nNAwUa+u52FW/P2BGrrVa0GEym9WWe1bEg==", "/yaS5u07mR2CSZTgQBn9hI/ZIPBORSZi5DSQ1fBGuX0=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("mariagrazia.pilera", new ResourceOwnerCredentialsEntity("mariagrazia.pilera", "wx44jKZ9bI6QJj4oXd6usq5JD0Y0QS6HbLbCVjjup+PZJoBGFoo7YPARf7kspzServP/1jAftXk5pxdl+AjOTg==", "3ImmegMS4gD/PEAbeUC6tuTknaT8RXrodxCIDpJHBf8=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("antonio.tarricone", new ResourceOwnerCredentialsEntity("antonio.tarricone", "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==", "WqWUNtojiV13mN8mF22mp5V8L61B323lBcm8OAU52No=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("alessandro.conti", new ResourceOwnerCredentialsEntity("alessandro.conti", "UVFoSPVcZdX6NUnqFH/crk9WUExwZ3CT70GgieuHKmGNPTwD5vGb85+NZtEF9L2PXnrbBNJrqyMfHm8IrPkMBw==", "bT0wHH7Exwt4lCUosg65DshWYuW/9ddl//zA4BAKcJI=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("stefano.menotti", new ResourceOwnerCredentialsEntity("stefano.menotti", "gRpzPiAe4OPCW/XT6Nr0ZEJljbTkpwwrCKgWWAfJsa0QRiEmj9G+hWjSAhoAGiSY3A6AhVxVYjBMl+wxuu6wwQ==", "xsirUBc3mgj12xqq3IT2SU+2bLdR0I3LW5/i8t1yMWY=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("jacopo.pompilii", new ResourceOwnerCredentialsEntity("jacopo.pompilii", "PpgpDo51l2QM6Kv/NKTpLPGJc5ofNcZydUDv8sE2VdHIUg8Ql9pSOd4aY0Uj7/Fp2mIWlhylTXxVzzPYaFdgMw==", "UmDDeb13DmlWhPdwxH8CFNdlxwqeD8DjZ5/SZsk5Iyo=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("elisa.riboli", new ResourceOwnerCredentialsEntity("elisa.riboli", "9hN79PDzNZy0fnkYcKUITJGt8xu4QndLcnXc6saO3w0KfaK5XVaeEJXkP8bQQ2tfJ2Pf5XKKrdxri90vPDLLeg==", "r2ahvlFauge4TyRIUKWffrohp8UzI2U2yW3gb09thKs=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("carmine.cicchino", new ResourceOwnerCredentialsEntity("carmine.cicchino", "9uYSTlx08FTEXxj7SVP4PULD1vXm66mQlPAEGqrFLDvGGTnUQAN4rqPjlDMqnZ6M247jruYXHgwBkdiGVodp+w==", "Xh3Kj06ZQ9DjRHjZLNPbF0iv4AjkohkzDPu0rFTta7U=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("stefano.repetti", new ResourceOwnerCredentialsEntity("stefano.repetti", "xPQW9HWb8VsqJU/y1MrjnI/SQ/4WEh46tfs8OXYFAs+/B4mKZwItM8fNz2UzJTHrY9MBrCBuZusufqI0I7j/QA==", "vib3EHr8o0uF2z/pdu/3pOgOcczkmbmwbV4D5seMB8o=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("francesca.pacenti", new ResourceOwnerCredentialsEntity("francesca.pacenti", "Gm3EFIg0VPnEzEJghqTG+Z0m07qB2zv711gliVBUsoOj/1riVYj26XiBTx4uUyA4aKmHa8Xh8gQWdb0HiOGWTQ==", "w8cpCXLTz8Nrm5aGHuofPSNQ5I7Kz5VNjDzWCWFsn4g=", "4585625", "POS", "28405fHfk73x88D")),
		
		Map.entry("carlodeche1", new ResourceOwnerCredentialsEntity("carlodeche1", "1uw3ZCkpjKlocU+8BtIT+kvgtAvMYxQduvk9g7TGMPU3tzFtEwodvgZPZ6HsdinW8rH3ldXZK0af3lJngSgPvg==", "gqQojcEKK7CQd1qLIJNmz6XvZKZsyG0rQx3KJCOmAjI=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("alessandro1", new ResourceOwnerCredentialsEntity("alessandro1", "fQbg+e0yPvw5Qk+kPe+epkYnrXBBgWha95lsVZKQydDfE07kyQl8nNAwUa+u52FW/P2BGrrVa0GEym9WWe1bEg==", "/yaS5u07mR2CSZTgQBn9hI/ZIPBORSZi5DSQ1fBGuX0=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("mariagrazi1", new ResourceOwnerCredentialsEntity("mariagrazi1", "wx44jKZ9bI6QJj4oXd6usq5JD0Y0QS6HbLbCVjjup+PZJoBGFoo7YPARf7kspzServP/1jAftXk5pxdl+AjOTg==", "3ImmegMS4gD/PEAbeUC6tuTknaT8RXrodxCIDpJHBf8=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("antoniotar1", new ResourceOwnerCredentialsEntity("antoniotar1", "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==", "WqWUNtojiV13mN8mF22mp5V8L61B323lBcm8OAU52No=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("stefanomen1", new ResourceOwnerCredentialsEntity("stefanomen1", "gRpzPiAe4OPCW/XT6Nr0ZEJljbTkpwwrCKgWWAfJsa0QRiEmj9G+hWjSAhoAGiSY3A6AhVxVYjBMl+wxuu6wwQ==", "xsirUBc3mgj12xqq3IT2SU+2bLdR0I3LW5/i8t1yMWY=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("jacopopomp1", new ResourceOwnerCredentialsEntity("jacopopomp1", "PpgpDo51l2QM6Kv/NKTpLPGJc5ofNcZydUDv8sE2VdHIUg8Ql9pSOd4aY0Uj7/Fp2mIWlhylTXxVzzPYaFdgMw==", "UmDDeb13DmlWhPdwxH8CFNdlxwqeD8DjZ5/SZsk5Iyo=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("elisaribol1", new ResourceOwnerCredentialsEntity("elisaribol1", "9hN79PDzNZy0fnkYcKUITJGt8xu4QndLcnXc6saO3w0KfaK5XVaeEJXkP8bQQ2tfJ2Pf5XKKrdxri90vPDLLeg==", "r2ahvlFauge4TyRIUKWffrohp8UzI2U2yW3gb09thKs=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("carminecic1", new ResourceOwnerCredentialsEntity("carminecic1", "9uYSTlx08FTEXxj7SVP4PULD1vXm66mQlPAEGqrFLDvGGTnUQAN4rqPjlDMqnZ6M247jruYXHgwBkdiGVodp+w==", "Xh3Kj06ZQ9DjRHjZLNPbF0iv4AjkohkzDPu0rFTta7U=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("stefanorep1", new ResourceOwnerCredentialsEntity("stefanorep1", "xPQW9HWb8VsqJU/y1MrjnI/SQ/4WEh46tfs8OXYFAs+/B4mKZwItM8fNz2UzJTHrY9MBrCBuZusufqI0I7j/QA==", "vib3EHr8o0uF2z/pdu/3pOgOcczkmbmwbV4D5seMB8o=", "4585625", "POS", "28405fHfk73x88D")),
		Map.entry("francescap1", new ResourceOwnerCredentialsEntity("francescap1", "Gm3EFIg0VPnEzEJghqTG+Z0m07qB2zv711gliVBUsoOj/1riVYj26XiBTx4uUyA4aKmHa8Xh8gQWdb0HiOGWTQ==", "w8cpCXLTz8Nrm5aGHuofPSNQ5I7Kz5VNjDzWCWFsn4g=", "4585625", "POS", "28405fHfk73x88D")),
		
		Map.entry("carlodeche2", new ResourceOwnerCredentialsEntity("carlodeche2", "1uw3ZCkpjKlocU+8BtIT+kvgtAvMYxQduvk9g7TGMPU3tzFtEwodvgZPZ6HsdinW8rH3ldXZK0af3lJngSgPvg==", "gqQojcEKK7CQd1qLIJNmz6XvZKZsyG0rQx3KJCOmAjI=", "4585625", "POS", "12346789")),
		Map.entry("alessandro2", new ResourceOwnerCredentialsEntity("alessandro2", "fQbg+e0yPvw5Qk+kPe+epkYnrXBBgWha95lsVZKQydDfE07kyQl8nNAwUa+u52FW/P2BGrrVa0GEym9WWe1bEg==", "/yaS5u07mR2CSZTgQBn9hI/ZIPBORSZi5DSQ1fBGuX0=", "4585625", "POS", "12346789")),
		Map.entry("mariagrazi2", new ResourceOwnerCredentialsEntity("mariagrazi2", "wx44jKZ9bI6QJj4oXd6usq5JD0Y0QS6HbLbCVjjup+PZJoBGFoo7YPARf7kspzServP/1jAftXk5pxdl+AjOTg==", "3ImmegMS4gD/PEAbeUC6tuTknaT8RXrodxCIDpJHBf8=", "4585625", "POS", "12346789")),
		Map.entry("antoniotar2", new ResourceOwnerCredentialsEntity("antoniotar2", "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==", "WqWUNtojiV13mN8mF22mp5V8L61B323lBcm8OAU52No=", "4585625", "POS", "12346789")),
		Map.entry("stefanomen2", new ResourceOwnerCredentialsEntity("stefanomen2", "gRpzPiAe4OPCW/XT6Nr0ZEJljbTkpwwrCKgWWAfJsa0QRiEmj9G+hWjSAhoAGiSY3A6AhVxVYjBMl+wxuu6wwQ==", "xsirUBc3mgj12xqq3IT2SU+2bLdR0I3LW5/i8t1yMWY=", "4585625", "POS", "12346789")),
		Map.entry("jacopopomp2", new ResourceOwnerCredentialsEntity("jacopopomp2", "PpgpDo51l2QM6Kv/NKTpLPGJc5ofNcZydUDv8sE2VdHIUg8Ql9pSOd4aY0Uj7/Fp2mIWlhylTXxVzzPYaFdgMw==", "UmDDeb13DmlWhPdwxH8CFNdlxwqeD8DjZ5/SZsk5Iyo=", "4585625", "POS", "12346789")),
		Map.entry("elisaribol2", new ResourceOwnerCredentialsEntity("elisaribol2", "9hN79PDzNZy0fnkYcKUITJGt8xu4QndLcnXc6saO3w0KfaK5XVaeEJXkP8bQQ2tfJ2Pf5XKKrdxri90vPDLLeg==", "r2ahvlFauge4TyRIUKWffrohp8UzI2U2yW3gb09thKs=", "4585625", "POS", "12346789")),
		Map.entry("carminecic2", new ResourceOwnerCredentialsEntity("carminecic2", "9uYSTlx08FTEXxj7SVP4PULD1vXm66mQlPAEGqrFLDvGGTnUQAN4rqPjlDMqnZ6M247jruYXHgwBkdiGVodp+w==", "Xh3Kj06ZQ9DjRHjZLNPbF0iv4AjkohkzDPu0rFTta7U=", "4585625", "POS", "12346789")),
		Map.entry("stefanorep2", new ResourceOwnerCredentialsEntity("stefanorep2", "xPQW9HWb8VsqJU/y1MrjnI/SQ/4WEh46tfs8OXYFAs+/B4mKZwItM8fNz2UzJTHrY9MBrCBuZusufqI0I7j/QA==", "vib3EHr8o0uF2z/pdu/3pOgOcczkmbmwbV4D5seMB8o=", "4585625", "POS", "12346789")),
		Map.entry("francescap2", new ResourceOwnerCredentialsEntity("francescap2", "Gm3EFIg0VPnEzEJghqTG+Z0m07qB2zv711gliVBUsoOj/1riVYj26XiBTx4uUyA4aKmHa8Xh8gQWdb0HiOGWTQ==", "w8cpCXLTz8Nrm5aGHuofPSNQ5I7Kz5VNjDzWCWFsn4g=", "4585625", "POS", "12346789")),
		
		Map.entry("carlodeche3", new ResourceOwnerCredentialsEntity("carlodeche3", "1uw3ZCkpjKlocU+8BtIT+kvgtAvMYxQduvk9g7TGMPU3tzFtEwodvgZPZ6HsdinW8rH3ldXZK0af3lJngSgPvg==", "gqQojcEKK7CQd1qLIJNmz6XvZKZsyG0rQx3KJCOmAjI=", "4585625", "POS", "999999600307")),
		Map.entry("alessandro3", new ResourceOwnerCredentialsEntity("alessandro3", "fQbg+e0yPvw5Qk+kPe+epkYnrXBBgWha95lsVZKQydDfE07kyQl8nNAwUa+u52FW/P2BGrrVa0GEym9WWe1bEg==", "/yaS5u07mR2CSZTgQBn9hI/ZIPBORSZi5DSQ1fBGuX0=", "4585625", "POS", "999999600307")),
		Map.entry("mariagrazi3", new ResourceOwnerCredentialsEntity("mariagrazi3", "wx44jKZ9bI6QJj4oXd6usq5JD0Y0QS6HbLbCVjjup+PZJoBGFoo7YPARf7kspzServP/1jAftXk5pxdl+AjOTg==", "3ImmegMS4gD/PEAbeUC6tuTknaT8RXrodxCIDpJHBf8=", "4585625", "POS", "999999600307")),
		Map.entry("antoniotar3", new ResourceOwnerCredentialsEntity("antoniotar3", "BhPEAxmNsm6JIidDZXl/jwIfuFUFwn/hjfoLnDuYyQEfUMQOrtlOCFljm8IYmN5OmMIh3RddWfNSJEVlRxZjig==", "WqWUNtojiV13mN8mF22mp5V8L61B323lBcm8OAU52No=", "4585625", "POS", "999999600307")),
		Map.entry("stefanomen3", new ResourceOwnerCredentialsEntity("stefanomen3", "gRpzPiAe4OPCW/XT6Nr0ZEJljbTkpwwrCKgWWAfJsa0QRiEmj9G+hWjSAhoAGiSY3A6AhVxVYjBMl+wxuu6wwQ==", "xsirUBc3mgj12xqq3IT2SU+2bLdR0I3LW5/i8t1yMWY=", "4585625", "POS", "999999600307")),
		Map.entry("jacopopomp3", new ResourceOwnerCredentialsEntity("jacopopomp3", "PpgpDo51l2QM6Kv/NKTpLPGJc5ofNcZydUDv8sE2VdHIUg8Ql9pSOd4aY0Uj7/Fp2mIWlhylTXxVzzPYaFdgMw==", "UmDDeb13DmlWhPdwxH8CFNdlxwqeD8DjZ5/SZsk5Iyo=", "4585625", "POS", "999999600307")),
		Map.entry("elisaribol3", new ResourceOwnerCredentialsEntity("elisaribol3", "9hN79PDzNZy0fnkYcKUITJGt8xu4QndLcnXc6saO3w0KfaK5XVaeEJXkP8bQQ2tfJ2Pf5XKKrdxri90vPDLLeg==", "r2ahvlFauge4TyRIUKWffrohp8UzI2U2yW3gb09thKs=", "4585625", "POS", "999999600307")),
		Map.entry("carminecic3", new ResourceOwnerCredentialsEntity("carminecic3", "9uYSTlx08FTEXxj7SVP4PULD1vXm66mQlPAEGqrFLDvGGTnUQAN4rqPjlDMqnZ6M247jruYXHgwBkdiGVodp+w==", "Xh3Kj06ZQ9DjRHjZLNPbF0iv4AjkohkzDPu0rFTta7U=", "4585625", "POS", "999999600307")),
		Map.entry("stefanorep3", new ResourceOwnerCredentialsEntity("stefanorep3", "xPQW9HWb8VsqJU/y1MrjnI/SQ/4WEh46tfs8OXYFAs+/B4mKZwItM8fNz2UzJTHrY9MBrCBuZusufqI0I7j/QA==", "vib3EHr8o0uF2z/pdu/3pOgOcczkmbmwbV4D5seMB8o=", "4585625", "POS", "999999600307")),
		Map.entry("francescap3", new ResourceOwnerCredentialsEntity("francescap3", "Gm3EFIg0VPnEzEJghqTG+Z0m07qB2zv711gliVBUsoOj/1riVYj26XiBTx4uUyA4aKmHa8Xh8gQWdb0HiOGWTQ==", "w8cpCXLTz8Nrm5aGHuofPSNQ5I7Kz5VNjDzWCWFsn4g=", "4585625", "POS", "999999600307")));

	/**
	 * 
	 * @param cliendId
	 * @return
	 */
	public Uni<Optional<ResourceOwnerCredentialsEntity>> findByIdOptional(String usename) {
		return Uni.createFrom().item(Optional.ofNullable(REPOSITORY.get(usename)));
	}
}
