package com.qbit.exchanger.dao.util;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @author Александр
 */
public final class DAOUtil {
	
	private DAOUtil() {
	}

	public static <T> T find(EntityManager entityManager, Class<T> entityClass, Object id, T empty) {
		try {
			T result = entityManager.find(entityClass, id);
			if (result != null) {
				return result;
			} else {
				return empty;
			}
		} catch (Exception ex) {
			return empty;
		}
	}

	public static <T> List<T> findAll(EntityManager entityManager, Class<T> entityClass) {
		CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(entityClass);
		criteriaQuery.select(criteriaQuery.from(entityClass));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	public static <T> List<T> findRange(EntityManager entityManager, Class<T> entityClass, int from, int to) {
		CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(entityClass);
		criteriaQuery.select(criteriaQuery.from(entityClass));
		TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
		query.setMaxResults(to - from + 1);
		query.setFirstResult(from);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static <T> int count(EntityManager entityManager, Class<T> entityClass) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));
		Query query = entityManager.createQuery(criteriaQuery);
		return ((Long) query.getSingleResult()).intValue();
	}
	
	public static <T> T invokeInTransaction(EntityManagerFactory entityManagerFactory,
			TrCallable<T> callable, int maxFailCount) {
		T result = null;
		int failCount = 0;
		while (failCount <= maxFailCount) {
			try {
				result = invokeInTransaction(entityManagerFactory, callable);
				break;
			} catch (Throwable ex) {
				failCount++;
				if (failCount > maxFailCount) {
					throw ex;
				}
			}
		}
		return result;
	}
	
	public static <T> T invokeInTransaction(EntityManagerFactory entityManagerFactory,
			TrCallable<T> callable) {
		T result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			result = callable.call(entityManager);
			entityManager.getTransaction().commit();
		} catch (Throwable ex) {
			try {
				entityManager.getTransaction().rollback();
			} catch (Throwable doNothing) {
			}
			throw ex;
		} finally {
			entityManager.close();
		}
		return result;
	}
}