package com.bitbucket.thinbus.srp6.spring;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSession;
import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Configuration
@EnableWebMvcSecurity
public class SrpSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SrpAuthenticationProvider srpAuthenticationProvider;

	@Bean
	public SrpAuthenticationProvider srpAuthenticationProvider() {
		return new SrpAuthenticationProvider();
	}

	@Value("${thinbus.N}")
	private String N;

	@Value("${thinbus.g}")
	private String g;

	@Bean
	LoadingCache<SrpAccountEntity, SRP6JavascriptServerSession> sessionCache() {
		return CacheBuilder
				.newBuilder()
				.expireAfterAccess(120, TimeUnit.SECONDS)
				.build(new CacheLoader<SrpAccountEntity, SRP6JavascriptServerSession>() {
					public SRP6JavascriptServerSession load(SrpAccountEntity key) {
						SRP6JavascriptServerSession session = new SRP6JavascriptServerSessionSHA256(
								N, g);
						session.step1(key.getEmail(), key.getSalt(),
								key.getVerifier());
						return session;
					}
				});
	}

    @Bean
    public SrpUserService userService() {
        return new SrpUserService();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices("remember-me-key", userService());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(srpAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
				.antMatchers("/", "/favicon.ico", "/resources/**", "/signup",
						"/register", "/challenge", "/dhparam")
				.permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/signin")
                .permitAll()
                .failureUrl("/signin?error=1")
                .loginProcessingUrl("/authenticate")
                .and()
            .logout()
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessUrl("/signin?logout")
                .and()
            .rememberMe()
                .rememberMeServices(rememberMeServices())
                .key("remember-me-key");
    }

}