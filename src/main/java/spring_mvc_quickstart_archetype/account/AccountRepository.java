package spring_mvc_quickstart_archetype.account;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bitbucket.thinbus.srp6.spring.SrpAccountEntity;

@Repository
@Transactional(readOnly = true)
public class AccountRepository {
	
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
