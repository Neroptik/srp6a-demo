package com.bitbucket.thinbus.srp6.spring;

public class SrpServerChallenge {
	private final String B;
	private final String salt;

	public SrpServerChallenge(String salt, String B) {
		this.salt = salt;
		this.B = B;
	}

	public String getSalt() {
		return salt;
	}

	public String getB() {
		return B;
	}
}
