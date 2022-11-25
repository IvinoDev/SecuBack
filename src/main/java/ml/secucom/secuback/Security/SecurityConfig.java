package ml.secucom.secuback.Security;


import lombok.RequiredArgsConstructor;
import ml.secucom.secuback.Filter.CustomAuthentificationFilter;
import ml.secucom.secuback.Filter.CustomAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Pour changer l'url de connexion par defaut
        CustomAuthentificationFilter customAuthentificationFilter = new CustomAuthentificationFilter(authenticationManagerBean());
        customAuthentificationFilter.setFilterProcessesUrl("/secuback/login");
        //Pour desactiver la session generee par defaut de Spring Security
        http.csrf().disable();
        http.cors().disable();
        //http.sessionManagement().sessionCreationPolicy(STATELESS);
        //Droit les pages accessible a tous
        http.formLogin();
        http.authorizeRequests().antMatchers( "/**" , "/secuback/login/**" , "/secuback/token/refresh/**").permitAll();
        //Gestion des droits d'acces aux differents endpoints en fonction des roles
        http.authorizeRequests().antMatchers(GET, "/secuback/collaborator/all", "/secuback/greetings")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/secuback/collaborator/add/**")
                .hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/secuback/role/add")
                .hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(POST, "/secuback/role/addToCollaborator")
                .hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/secuback/collaborator/edit/**")
                .hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(DELETE, "/secuback/login/delete/**")
                .hasAnyAuthority("ROLE_ADMIN")
                .and()
                .oauth2Login();
        http.authorizeRequests().antMatchers(POST, "/secuback/greetings");
        //Devoir etre connecter pour effectuer les choses declarees plus haut
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthentificationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
