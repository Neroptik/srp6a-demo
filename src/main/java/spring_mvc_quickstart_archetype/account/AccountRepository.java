package spring_mvc_quickstart_archetype.account;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bitbucket.thinbus.srp6.spring.SrpAccountEntity;

@Repository
@Transactional(readOnly = true)
public class AccountRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public SrpAccountEntity save(SrpAccountEntity account) {
		account.setVerifier(passwordEncoder.encode(account.getVerifier()));
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
