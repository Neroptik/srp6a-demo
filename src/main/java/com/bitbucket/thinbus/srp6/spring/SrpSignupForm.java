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

	public SrpAccountEntity createAccount() {
		return new SrpAccountEntity(getEmail(), getSalt(), getVerifier(),
				"ROLE_USER");
	}
}
