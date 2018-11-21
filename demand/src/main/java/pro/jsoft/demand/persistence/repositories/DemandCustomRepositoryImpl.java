package pro.jsoft.demand.persistence.repositories;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.util.StringUtils;

import lombok.val;
import lombok.var;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.rest.types.DemandFilter;
import pro.jsoft.demand.rest.types.Mode;
import pro.jsoft.demand.rest.types.PageableResultList;
import pro.jsoft.demand.rest.types.Pagination;
import pro.jsoft.spring.security.DetailedUser;

@Slf4j
public class DemandCustomRepositoryImpl implements DemandCustomRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public PageableResultList<Demand> findByMetadata(final String profile, final DemandFilter filter, DetailedUser user) {
		val specification = DemandSpecifications.matchFilter(profile, filter, user);
		var pagination = filter.getPagination();
		if (pagination == null) {
			pagination = new Pagination(1, 10, "id", true, null);
		}
		
		if (Mode.EXECUTOR.equals(filter.getMode()) && (user.getServicedBranch() == null || user.getServicedBranch().isEmpty())) {
			pagination.setRowsNumber(0);
			List<Demand> resultList = Collections.emptyList();
			return new PageableResultList<>(resultList, pagination);
		}
		
		val cb = em.getCriteriaBuilder();

		// count
		val cqCount = cb.createQuery(Long.class);
		val rootCount = cqCount.from(Demand.class);
		val predicateCount = specification.toPredicate(rootCount, cqCount, cb);
		if (predicateCount != null) {
			cqCount.where(predicateCount);
		}
		cqCount.select(cb.count(rootCount));
		val queryCount = em.createQuery(cqCount);
		log.debug(">>> Count query");
		val count = queryCount.getSingleResult();
		log.debug(">>> Count of rows: {}", count);
		pagination.setRowsNumber(count.intValue());
		
		if (pagination.getRowsNumber() == 0) {
			List<Demand> resultList = Collections.emptyList();
			return new PageableResultList<>(resultList, pagination);
		}
		
		// list
		val cqList = cb.createQuery(Demand.class);
		val rootList = cqList.from(Demand.class);
		val predicateList = specification.toPredicate(rootList, cqList, cb);
		if (predicateList != null) {
			cqList.where(predicateList);
		}
		cqList.select(rootList);
		if (StringUtils.hasText(pagination.getSortBy())) {
			val field = rootList.get(pagination.getSortBy());
		    val order = pagination.getDescending() 
		    				? cb.desc(field) 
		    				: cb.asc(field);
			cqList.orderBy(Collections.singletonList(order));
		}
		val queryList = em.createQuery(cqList);
		queryList.setFirstResult((pagination.getPage() - 1) * pagination.getRowsPerPage());
		queryList.setMaxResults(pagination.getRowsPerPage());
		log.debug(">>> List query");
		
		val resultList = queryList.getResultList();
		
		return new PageableResultList<>(resultList, pagination);
	}
}
