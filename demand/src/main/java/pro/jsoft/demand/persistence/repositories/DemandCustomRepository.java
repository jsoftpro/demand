package pro.jsoft.demand.persistence.repositories;

import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.rest.types.DemandFilter;
import pro.jsoft.demand.rest.types.PageableResultList;
import pro.jsoft.spring.security.DetailedUser;

public interface DemandCustomRepository {
	PageableResultList<Demand> findByMetadata(String profile, DemandFilter filter, DetailedUser user);
}
