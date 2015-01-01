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

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSession;
import com.google.common.cache.Cache;
import com.nimbusds.srp6.SRP6ServerSession;

/**
 * Performs user authentication based on the `M1` password proof using an
 * SRPServerSession created over AJAX and held in a local cache.
 */
public class SrpAuthenticationProvider implements AuthenticationProvider {
	static final Logger log = LoggerFactory
			.getLogger(SrpAuthenticationProvider.class);

	@Inject
	protected Cache<SrpAccountEntity, SRP6JavascriptServerSession> sessionCache;

	@Inject
	protected SrpAccountRepository repository;

	@Override
	public Authentication authenticate(Authentication authentication) {
		final String userId = authentication.getName();
		final String concatOfM1andA = authentication.getCredentials()
				.toString();

		final String[] arrayAandM1 = concatOfM1andA.split(":");

		if (arrayAandM1.length == 2) {
			final String M1 = arrayAandM1[0];
			final String A = arrayAandM1[1];
			try {
				final SrpAccountEntity ud = repository.findByEmail(userId);

				if (ud != null) {
					final SRP6JavascriptServerSession serverSession = sessionCache
							.getIfPresent(ud);

					if (serverSession != null) {
						// remove the server session as cannot be reused
						sessionCache.invalidate(ud);

						String state = serverSession.getState();

						if (SRP6ServerSession.State.STEP_1.name() == state) {
							// this step throws if the password proof fails
							serverSession.step2(A, M1);

							// great the user is authenticated
							List<GrantedAuthority> grantedAuths = new ArrayList<>();
							grantedAuths.add(new SimpleGrantedAuthority(ud
									.getRole()));
							Authentication auth = new UsernamePasswordAuthenticationToken(
									userId, null, grantedAuths);
							log.info("user {} authenticated via SRP", userId);
							return auth;
						} else {
							log.trace(
									"user srp session for '{}' not advanced to state STEP_1 via ajax so unable to complete authentication with form post",
									userId);
							return null;
						}

					} else {
						log.trace(
								"user {} does not have a SRPServerSession in the temporary cache",
								userId);
						return null;
					}

				} else {
					log.trace("user '{}' not found in repository", userId);
					return null;
				}

			} catch (Throwable t) {
				log.trace(
						"SrpAuthenticationProvider saw exception most likely due to failed srp verification step: {} '{}'",
						t.getClass().getCanonicalName(), t.getMessage());
				return null;
			}
		} else {
			log.trace("user {} credentials is not in 'M1':'A' format", userId);
			return null;
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
