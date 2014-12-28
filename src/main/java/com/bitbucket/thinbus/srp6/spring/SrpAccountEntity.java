package com.bitbucket.thinbus.srp6.spring;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This is both an entity and a cached key
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "account")
@NamedQuery(name = SrpAccountEntity.FIND_BY_EMAIL, query = "select a from SrpAccountEntity a where a.email = :email")
public class SrpAccountEntity implements java.io.Serializable {

	public static final String FIND_BY_EMAIL = "SrpAccountEntity.findByEmail";

	@Id
	@GeneratedValue
	private Long id; // TODO get rid of this and make the email the id

	@Column(unique = true)
	private String email;
	
	@Column(unique = true)
	private String salt;

	@JsonIgnore // TODO hangover from being a password? 
	@Column(unique = true)
	private String verifier;

	// TODO why does this have to be a member with a setter not some read-only
	// constant?
	private String role = "ROLE_USER";

    protected SrpAccountEntity() {

	}
	
	public SrpAccountEntity(String email, String verifier, String role) {
		this.email = email;
		this.verifier = verifier;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result
				+ ((verifier == null) ? 0 : verifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SrpAccountEntity other = (SrpAccountEntity) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (verifier == null) {
			if (other.verifier != null)
				return false;
		} else if (!verifier.equals(other.verifier))
			return false;
		return true;
	}
}
