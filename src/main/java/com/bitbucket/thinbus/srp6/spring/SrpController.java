package com.bitbucket.thinbus.srp6.spring;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.bitbucket.thinbus.srp6.js.HexHashedRoutines;
import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring_mvc_quickstart_archetype.support.web.MessageHelper;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSession;
import com.google.common.cache.LoadingCache;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller
public class SrpController {

	@Inject
	protected SrpSecurityConfig config;

	@Inject
	protected LoadingCache<SrpAccountEntity, SRP6JavascriptServerSession> sessionCache;

	@Inject
	protected SrpAccountRepository accountRepository;

	@Autowired
	private SrpUserService userService;

	private static final String SIGNUP_VIEW_NAME = "signup/signup";

	@RequestMapping(value = "signup")
	public String signup(Model model) {
		model.addAttribute(new SrpSignupForm());
		return SIGNUP_VIEW_NAME;
	}

	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SrpSignupForm signupForm,
			Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return SIGNUP_VIEW_NAME;
		}
        SrpAccountEntity account = accountRepository.save(signupForm
				.createAccount());
		userService.signin(account);
		// see /WEB-INF/i18n/messages.properties and
		// /WEB-INF/views/homeSignedIn.html
		MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:/";
	}

	static MessageDigest sha256() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException e) {
			throw new AssertionError("not possible in jdk1.7 and 1.8: "+e);
		}
	}

	MessageDigest md = sha256();

	String hash(String value) {
		try {
			md.update(value.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("not possible in jdk1.7 and 1.8: "+e);
		}
		return HexHashedRoutines.toHexString(md.digest());
	}

	/**
	 * Returns the user salt and SRP challenge for the user to perform their password-proof.
	 * We don't want to leak to an attacker which users are or are not registered with the site.
	 * So we return a fake salt and challenge when there is no such user in the database.
	 * The method is deliberately de-optimised to try to make it the same speed for both
	 * scenarios.
	 */
	@RequestMapping(value = "challenge", method = RequestMethod.POST)
	public @ResponseBody SrpServerChallenge challenge(
			HttpServletRequest request,
			@RequestParam(value = "email", required = true) String email,
			Model model) {

		final String fakeSalt = hash(config.saltOfFakeSalt+email);

		final SrpAccountEntity realAccount = accountRepository.findByEmail(email);

		if (realAccount != null) {
			SRP6JavascriptServerSession srpSession = sessionCache
					.getUnchecked(realAccount);
			return new SrpServerChallenge(realAccount.getSalt(),
					srpSession.getPublicServerValue());
		} else {
			final SrpAccountEntity fakeAccount = new SrpAccountEntity(email, fakeSalt);

			final SRP6JavascriptServerSession fakeSession = new SRP6JavascriptServerSessionSHA256(
					config.N, config.g);

			fakeSession.step1(email, fakeSalt, "0");

			return new SrpServerChallenge(fakeSalt, fakeSession.getPublicServerValue());

		}
	}
}
