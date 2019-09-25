package it.polito.ai.project.securityConfig;

import it.polito.ai.project.security.JwtConfigurer;
import it.polito.ai.project.security.JwtTokenProvider;
import it.polito.ai.project.services.database.models.Roles;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

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
                .antMatchers("/auth/confirm/**").permitAll()
                .antMatchers("/auth/recovery/**").permitAll()
                .antMatchers("/auth/recovery/reset").permitAll()
                .antMatchers("/auth/register").permitAll()
                .antMatchers("/credentials/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/lines/{line_name}/races/{date}/{direction}").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers(HttpMethod.GET, "/lines/{line_name}/races/**").authenticated()
                .antMatchers(HttpMethod.POST, "/lines/{line_name}/races").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers(HttpMethod.PUT, "/lines/{line_name}").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers(HttpMethod.POST, "/lines/{line_name}/races/{date}/{direction}/validate").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers("/lines/**").permitAll()
                .antMatchers("/users/**").authenticated()
                .antMatchers("/parent/**").authenticated()
                .antMatchers("/companion/**").hasRole(Roles.COMPANION)
                .antMatchers("/admin/**").hasAnyRole(Roles.ADMIN, Roles.SYSTEM_ADMIN)
                .antMatchers("/ws/**").permitAll()
                .anyRequest().authenticated()
                //TODO: remove permitAll
                //.anyRequest().permitAll()
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
