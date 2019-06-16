package it.polito.ai.lab5.config;

import it.polito.ai.lab5.security.JwtConfigurer;
import it.polito.ai.lab5.security.JwtTokenProvider;
import it.polito.ai.lab5.services.database.models.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/auth/register").permitAll()
                .antMatchers("/auth/confirm").permitAll()
                .antMatchers("/auth/recover").permitAll()
                .antMatchers("/auth/recover/*").permitAll()
                .antMatchers(HttpMethod.GET, "/users").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers(HttpMethod.PUT, "/users/*").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers(HttpMethod.GET, "/lines").hasRole(Roles.USER)
                .antMatchers(HttpMethod.GET, "/lines/*").hasRole(Roles.USER)
                .antMatchers(HttpMethod.GET, "/lines_names").hasRole(Roles.USER)
                .antMatchers(HttpMethod.GET, "/reservations").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers(HttpMethod.GET, "/reservations/**").hasAnyRole(Roles.USER)
                .antMatchers(HttpMethod.POST, "/reservations/**").hasAnyRole(Roles.USER)
                .antMatchers(HttpMethod.PUT, "/reservations/**").hasAnyRole(Roles.USER)
                .antMatchers(HttpMethod.DELETE, "/reservations/**").hasAnyRole(Roles.USER)
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

    @Bean
    public WebMvcConfigurer corsConfigurator() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

}
