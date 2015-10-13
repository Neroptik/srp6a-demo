package com.bitbucket.thinbus.srp6.spring;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class SrpAccountRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public SrpAccountEntity save(SrpAccountEntity account) {
		entityManager.persist(account);
		return account;
	}
	
	public SrpAccountEntity findByEmail(String email) {
		try {
			return entityManager.createNamedQuery(SrpAccountEntity.FIND_BY_EMAIL, SrpAccountEntity.class)
					.setParameter("email", email)
					.getSingleResult();
		} catch (PersistenceException e) {
			return null;
		}
	}

	
}
