package pro.jsoft.demand.persistence.repositories;

import java.util.Arrays;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import lombok.val;
import lombok.var;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Demand_;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.persistence.model.Stage_;
import pro.jsoft.demand.rest.types.DemandFilter;
import pro.jsoft.demand.rest.types.Mode;
import pro.jsoft.spring.security.DetailedUser;

@Slf4j
public class DemandSpecifications {
	private DemandSpecifications() {
	}
	
	static Predicate and(CriteriaBuilder cb, Predicate a, Predicate b) {
		return (a != null) ? cb.and(a, b) : b;
	}
	
	static Predicate or(CriteriaBuilder cb, Predicate a, Predicate b) {
		return (a != null) ? cb.or(a, b) : b;
	}
	
	public static Specification<Demand> matchFilter(
										final String profile, 
										final DemandFilter filter,
										final DetailedUser user) {
		return new FilteredDemandListSpecification(profile, filter, user);
	}
	
	static class FilteredDemandListSpecification implements Specification<Demand> {
		private static final long serialVersionUID = 1L;
		
		private final DemandFilter filter;
		private final String profile;
		private final DetailedUser user;
		
		public FilteredDemandListSpecification(
				final String profile, 
				final DemandFilter filter,
				final DetailedUser user) {
			this.filter = filter;
			this.profile = profile;
			this.user = user;
		}

		@Override
		public Predicate toPredicate(final Root<Demand> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
			// Filter by root attributes
			log.debug("Demand filter: []", filter.toString());

			var completePredicate = cb.equal(root.get(Demand_.PROFILE), profile);
			
			if (filter.getId() != null) {
				val predicate = cb.equal(root.get(Demand_.ID), filter.getId());
				completePredicate = DemandSpecifications.and(cb, completePredicate, predicate);
			}

			if (StringUtils.hasText(filter.getName())) {
				val param = "%" + filter.getName().toLowerCase() + "%";
				val predicate = cb.like(
						cb.lower(root.get(Demand_.NAME)), 
						param);
			
				completePredicate = DemandSpecifications.and(cb, completePredicate, predicate);
			}
						
			if (StringUtils.hasText(filter.getFormName())) {
				val param = filter.getFormName();
				val predicate = cb.equal(
						root.get(Demand_.FORM_NAME), 
						param);
			
				completePredicate = DemandSpecifications.and(cb, completePredicate, predicate);
			}

			var addAuthorMode = false;
			
			if (Mode.EXECUTOR.equals(filter.getMode())) {
				Predicate predicateBranch = null;
				for (var branch : user.getServicedBranch()) {
					val param = branch + "%"; 
					val predicate = cb.like(
						root.get(Demand_.BRANCH), 
						param);
					
					predicateBranch = DemandSpecifications.or(cb, predicateBranch, predicate);
				}
				
				completePredicate = DemandSpecifications.and(cb, completePredicate, predicateBranch);
			} else if (filter.getMode() == null) {
				addAuthorMode = true;
			}

			// Filter by the first Stage
			
			if (addAuthorMode) {
				filter.setMode(Mode.AUTHOR);
			}
			
			val subqueryCreated = filterByCreated(query, cb);
			if (subqueryCreated.isPresent()) {
				val predicateCreated = cb.in(root.get(Demand_.ID)).value(subqueryCreated.get());
				if (addAuthorMode) {
					// by default list filtered for author or subject
					val param = user.getUsername();
					val predicateEmployeeUid = cb.equal(
							root.get(Demand_.UID), 
							param);
					val predicateEmployee = DemandSpecifications.or(cb, predicateEmployeeUid, predicateCreated);
					completePredicate = DemandSpecifications.and(cb, completePredicate, predicateEmployee);
				} else {
					completePredicate = DemandSpecifications.and(cb, completePredicate, predicateCreated);
				}
			}

			if (addAuthorMode) {
				filter.setMode(null);
			}
			
			// Filter by the last Stage

			val subqueryChanged = filterByChanged(query, cb);
			if (subqueryChanged.isPresent()) {
				completePredicate = DemandSpecifications.and(cb, 
						completePredicate, 
						cb.in(root.get(Demand_.ID)).value(subqueryChanged.get()));
			}

			return completePredicate;
		}
		
		private Optional<Subquery<Long>> filterByCreated(CriteriaQuery<?> query, final CriteriaBuilder cb) {
			Predicate predicateCreated = null;
			val subqueryCreated = query.subquery(Long.class);
			val rootCreated = subqueryCreated.from(Stage.class);
			
			
			if (filter.getCreatedAt() != null) {
				if (filter.getCreatedAt().getFrom() != null) {
					val predicate = cb.greaterThanOrEqualTo(
							rootCreated.get(Stage_.DATE), 
							filter.getCreatedAt().getFrom());

					predicateCreated = DemandSpecifications.and(cb, predicateCreated, predicate); 
				}

				if (filter.getCreatedAt().getTo() != null) {
					val predicate = cb.lessThanOrEqualTo(
							rootCreated.get(Stage_.DATE), 
							filter.getCreatedAt().getTo());

					predicateCreated = DemandSpecifications.and(cb, predicateCreated, predicate);
				}
			}

			if (StringUtils.hasText(filter.getCreatedBy())) {
				val param = "%" + filter.getCreatedBy().toLowerCase() + "%";
				val predicate = cb.like(
						cb.lower(rootCreated.get(Stage_.ACTOR_NAME)), 
						param);

				predicateCreated = DemandSpecifications.and(cb, predicateCreated, predicate);
			}

			if (Mode.AUTHOR.equals(filter.getMode())) {
				val param = user.getUsername();
				val predicate = cb.equal(
						rootCreated.get(Stage_.ACTOR_UID), 
						param);

				predicateCreated = DemandSpecifications.and(cb, predicateCreated, predicate);
			}

			if (predicateCreated != null) {
				val subqueryCreatedId = subqueryCreated.subquery(Long.class);
				val rootCreatedId = subqueryCreatedId.from(Stage.class);
				Expression<Long> minId = cb.min(rootCreatedId.get(Stage_.ID));
				subqueryCreatedId.select(minId).groupBy(rootCreatedId.get(Stage_.DEMAND));

				predicateCreated = DemandSpecifications.and(cb, predicateCreated, 
						cb.in(rootCreated.get(Stage_.ID)).value(subqueryCreatedId));
				subqueryCreated.select(rootCreated.get(Stage_.DEMAND)).where(predicateCreated);
				
				return Optional.of(subqueryCreated);
			}

			return Optional.empty();
		}
		
		
		private Optional<Subquery<Long>> filterByChanged(CriteriaQuery<?> query, final CriteriaBuilder cb) {
			Predicate predicateChanged = null;
			val subqueryChanged = query.subquery(Long.class);
			val rootChanged = subqueryChanged.from(Stage.class);

			if (filter.getChangedAt() != null) {
				if (filter.getChangedAt().getFrom() != null) {
					val predicate = cb.greaterThanOrEqualTo(
							rootChanged.get(Stage_.DATE), 
							filter.getChangedAt().getFrom());

					predicateChanged = DemandSpecifications.and(cb, predicateChanged, predicate); 
				}

				if (filter.getChangedAt().getTo() != null) {
					val predicate = cb.lessThanOrEqualTo(
							rootChanged.get(Stage_.DATE), 
							filter.getChangedAt().getTo());

					predicateChanged = DemandSpecifications.and(cb, predicateChanged, predicate);
				}
			}

			if (StringUtils.hasText(filter.getChangedBy())) {
				val param = "%" + filter.getChangedBy().toLowerCase() + "%";
				val predicate = cb.like(
						cb.lower(rootChanged.get(Stage_.ACTOR_NAME)), 
						param);

				predicateChanged = DemandSpecifications.and(cb, predicateChanged, predicate);
			}
			
			if (Mode.APPROVER.equals(filter.getMode())) {
				val param = user.getUsername();
				val predicate = cb.equal(
						rootChanged.get(Stage_.RECIPIENT_UID), 
						param);
			
				predicateChanged = DemandSpecifications.and(cb, predicateChanged, predicate);
			} else if (Mode.EXECUTOR.equals(filter.getMode())) {
				val predicate = cb.not(rootChanged.get(Stage_.CODE).in(
						Arrays.asList(Action.STEP_CREATE.getCode() /*, there could be several actions */)));
				predicateChanged = DemandSpecifications.and(cb, predicateChanged, predicate);
			}
			
			if (filter.getStageName() != null) {
				val predicate = cb.equal(rootChanged.get(Stage_.CODE), filter.getStageName().getCode());
				predicateChanged = DemandSpecifications.and(cb, predicateChanged, predicate);
			}
			
			if (predicateChanged != null) {
				val subqueryChangedId = subqueryChanged.subquery(Long.class);
				val rootChangedId = subqueryChangedId.from(Stage.class);
				Expression<Long> maxId = cb.max(rootChangedId.get(Stage_.ID));
				subqueryChangedId.select(maxId).groupBy(rootChangedId.get(Stage_.DEMAND));

				predicateChanged = DemandSpecifications.and(cb, predicateChanged, 
						cb.in(rootChanged.get(Stage_.ID)).value(subqueryChangedId));
				subqueryChanged.select(rootChanged.get(Stage_.DEMAND)).where(predicateChanged);
				
				return Optional.of(subqueryChanged);
			}
			
			return Optional.empty();
		}
	}
}
