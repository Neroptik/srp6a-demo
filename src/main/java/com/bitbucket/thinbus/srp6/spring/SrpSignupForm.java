package com.bitbucket.thinbus.srp6.spring;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class SrpSignupForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

    @NotBlank(message = SrpSignupForm.NOT_BLANK_MESSAGE)
	@Email(message = SrpSignupForm.EMAIL_MESSAGE)
	private String email;

    @NotBlank(message = SrpSignupForm.NOT_BLANK_MESSAGE)
	private String salt;

	@NotBlank(message = SrpSignupForm.NOT_BLANK_MESSAGE)
	private String verifier;

	@NotBlank(message = SrpSignupForm.NOT_BLANK_MESSAGE)
	private String pgppriv;
	
    @NotBlank(message = SrpSignupForm.NOT_BLANK_MESSAGE)
	private String pgppub;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getPgppriv() {
		return pgppriv;
	}

	public void setPgppriv(String pgppriv) {
		this.pgppriv = pgppriv;
	}

	public String getPgppub() {
		return pgppub;
	}

	public void setPgppub(String pgppub) {
		this.pgppub = pgppub;
	}

	public SrpAccountEntity createAccount() {
		return new SrpAccountEntity(getEmail(), getSalt(), getVerifier(), getPgppriv(), getPgppub(),
				"ROLE_USER");
	}
}
