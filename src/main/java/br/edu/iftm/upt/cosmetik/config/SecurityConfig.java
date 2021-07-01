package br.edu.iftm.upt.cosmetik.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// Qualquer um pode fazer requisições para essas URLs
				.antMatchers("/css/**", "/js/**", "/images/**", "/", "/index.html").permitAll()
				// Um usuário autenticado e com o papel ADMIN pode fazer requisições para essas
				// URLs
				//.antMatchers("/**").hasRole("ADMIN")
				.antMatchers("/**").permitAll()
				//.antMatchers("/usuario/novo").hasRole("ADMIN")
				//.anyRequest().authenticated()
				// .antMatchers("URL").hasAnyRole("ADMIN", "USUARIO")
				.and()
				// A autenticação usando formulário está habilitada
				.formLogin()
				.defaultSuccessUrl("/sessionauthenticate");
	}

	// Autenticacao JDBC
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select usuario, senha, ativo " + "from usuario " + "where usuario = ?")
				.authoritiesByUsernameQuery("SELECT tab.usuario, papel.nome from"
						+ "(SELECT usuario.usuario, usuario.codigo from usuario where usuario = ? ) as tab "
						+ " inner join usuario_papel on codigo_usuario = tab.codigo \n"
						+ " inner join papel on codigo_papel = papel.codigo;")
				.passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		String idEncoder = "bcrypt";
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		// encoders.put("argon2", new Argon2PasswordEncoder());
		encoders.put("noop", NoOpPasswordEncoder.getInstance());
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		// encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		// encoders.put("scrypt", new SCryptPasswordEncoder());
		// encoders.put("sha256", new StandardPasswordEncoder());
		PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(idEncoder, encoders);

		return passwordEncoder;
	}

}
