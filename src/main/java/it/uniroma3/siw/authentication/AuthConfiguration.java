package it.uniroma3.siw.authentication;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.uniroma3.siw.model.Credentials;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
				.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable()).authorizeHttpRequests(auth -> auth
				// 1) Pagine pubbliche (anonimo)
				.requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/books", "/books/**", "/book/cover/**",
						"/author/image/**", "/authors", "/authors/**", "/reviews", "/css/**", "/images/**", "/search",
						"/search/**", "/authors/search", "/books/search", "/profile", "/profile/manage",
						"/profile/credentials", "/js/**", "/favicon.ico")
				.permitAll().requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()

				// 2) Solo ADMIN sui path /admin/**
				.requestMatchers("/admin/**").hasAuthority(Credentials.ADMIN_ROLE)

				// 3) Solo USER (utente registrato) sui path /user/**
				.requestMatchers("/profile/manage", "/profile", "/profile/credentials")
				.hasAuthority(Credentials.USER_ROLE)

				// 4) Tutte le altre richieste richiedono autenticazione
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/", true)
						.failureUrl("/login?error=true"))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
						.deleteCookies("JSESSIONID").logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.clearAuthentication(true).permitAll());

		return httpSecurity.build();
	}
}