package com.bitbucket.thinbus.srp6.spring;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import spring_mvc_quickstart_archetype.account.AccountRepository;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSession;
import com.google.common.cache.LoadingCache;
import com.nimbusds.srp6.SRP6ServerSession;

public class SrpAuthenticationProvider implements AuthenticationProvider {
	static final Logger log = LoggerFactory
			.getLogger(SrpAuthenticationProvider.class);

	@Inject
	protected LoadingCache<SrpAccountEntity, SRP6JavascriptServerSession> sessionCache;

	@Inject
	protected AccountRepository repository;

	@Override
	public Authentication authenticate(Authentication authentication) {
		final String userId = authentication.getName();
		final String concatOfM1andA = authentication.getCredentials()
				.toString();
		try {
			final SrpAccountEntity ud = repository.findByEmail(userId);

			if (ud != null) {
				final SRP6JavascriptServerSession serverSession = sessionCache
						.get(ud);

				final String[] arrayAandM1 = concatOfM1andA.split(":");

				if (arrayAandM1.length == 2) {
					final String M1 = arrayAandM1[0];
					final String A = arrayAandM1[1];

					String state = serverSession.getState();

					if (SRP6ServerSession.State.STEP_1.name() == state) {
						// this step throws if the password proof fails
						serverSession.step2(A, M1);
						List<GrantedAuthority> grantedAuths = new ArrayList<>();
						grantedAuths
								.add(new SimpleGrantedAuthority("ROLE_USER"));
						Authentication auth = new UsernamePasswordAuthenticationToken(
								userId, null, grantedAuths);
						return auth;
					} else {
						log.info(
								"user session for '{}' not advanced to state STEP_1 via ajax so unable to complete authentication with form post",
								userId);
						return null;
					}
				} else {
					log.info("user {} credentials is not in 'M1':'A' format",
							userId);
					return null;
				}
			} else {
				log.info("user '{}' not found in repository", userId);
				return null;
			}

		} catch (Throwable t) {
			log.info(
					"SrpAuthenticationProvider saw exception most likely due to failed srp verification step:",
					t);
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
