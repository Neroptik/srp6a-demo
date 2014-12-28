package com.bitbucket.thinbus.srp6.spring;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import spring_mvc_quickstart_archetype.account.AccountRepository;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSession;
import com.google.common.cache.LoadingCache;

@Controller
public class SrpController {

	@Inject
	protected LoadingCache<SrpAccountEntity, SRP6JavascriptServerSession> sessionCache;

	@Inject
	protected AccountRepository repository;

	@RequestMapping("/")
	public String home(Model model) {
		return "index";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public @ResponseBody SrpAccountEntity register(
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password_salt", required = true) String password_salt,
			@RequestParam(value = "password_verifier", required = true) String password_verifier,
			Model model) {
		SrpAccountEntity ud = new SrpAccountEntity(email, password_salt,
				password_verifier);
		repository.save(ud);
		return ud;
	}

	@RequestMapping(value = "/challenge", method = RequestMethod.POST)
	public @ResponseBody SrpServerChallenge challenge(HttpServletRequest request,
			@RequestParam(value = "email", required = true) String email,
			Model model) {

		final SrpAccountEntity key = repository.findByEmail(email);

		SRP6JavascriptServerSession srpSession = sessionCache.getUnchecked(key);

		return new SrpServerChallenge(key.getSalt(),
				srpSession.getPublicServerValue());
	}

}
