package spring_mvc_quickstart_archetype.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import spring_mvc_quickstart_archetype.account.UserService;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSession;
import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import com.bitbucket.thinbus.srp6.spring.SrpAccountEntity;
import com.bitbucket.thinbus.srp6.spring.SrpAuthenticationProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Configuration
@EnableWebMvcSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SrpAuthenticationProvider srpAuthenticationProvider;

	@Bean
	public SrpAuthenticationProvider srpAuthenticationProvider() {
		return new SrpAuthenticationProvider();
	}

	String N() { // TODO externalize this config
		return "19502997308733555461855666625958719160994364695757801883048536560804281608617712589335141535572898798222757219122180598766018632900275026915053180353164617230434226106273953899391119864257302295174320915476500215995601482640160424279800690785793808960633891416021244925484141974964367107";
	}

	String g() { // TODO externalize this config
		return "2";
	}

	int timeoutSeconds() { // TODO externalize this config
		return 120;
	}

	@Bean
	LoadingCache<SrpAccountEntity, SRP6JavascriptServerSession> sessionCache() {
		return CacheBuilder
				.newBuilder()
				.expireAfterAccess(timeoutSeconds(), TimeUnit.SECONDS)
				.build(new CacheLoader<SrpAccountEntity, SRP6JavascriptServerSession>() {
					public SRP6JavascriptServerSession load(SrpAccountEntity key) {
						SRP6JavascriptServerSession session = new SRP6JavascriptServerSessionSHA256(
								N(), g());
						session.step1(key.getEmail(), key.getSalt(),
								key.getVerifier());
						return session;
					}
				});
	}

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices("remember-me-key", userService());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
	}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(srpAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		// TODO remove '/signup'?
        http
            .authorizeRequests()
				.antMatchers("/", "/favicon.ico", "/resources/**", "/signup",
						"/register", "/challenge")
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