package com.bitbucket.thinbus.srp6.spring;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class SrpController {

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

	@RequestMapping(value = "challenge", method = RequestMethod.POST)
	public @ResponseBody SrpServerChallenge challenge(HttpServletRequest request,
			@RequestParam(value = "email", required = true) String email,
			Model model) {

		final SrpAccountEntity key = accountRepository.findByEmail(email);

		SRP6JavascriptServerSession srpSession = sessionCache.getUnchecked(key);

		return new SrpServerChallenge(key.getSalt(),
				srpSession.getPublicServerValue());
	}

}
