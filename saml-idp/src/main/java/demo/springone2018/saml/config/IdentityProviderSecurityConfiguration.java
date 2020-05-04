/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package demo.springone2018.saml.config;

import static org.springframework.security.saml.provider.identity.config.SamlIdentityProviderSecurityDsl.identityProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml.provider.identity.config.SamlIdentityProviderSecurityConfiguration;

@EnableWebSecurity
public class IdentityProviderSecurityConfiguration {

	@Configuration
	@Order(1)
	public static class SamlSecurity extends SamlIdentityProviderSecurityConfiguration {

		private final AppConfig appConfig;
		private final BeanConfig beanConfig;
		private final CustomUserDetailsService userDetailsService;

		public SamlSecurity(BeanConfig beanConfig, @Qualifier("appConfig") AppConfig appConfig,CustomUserDetailsService userDetailsService) {
			super("/saml/idp/", beanConfig);
			this.appConfig = appConfig;
			this.beanConfig = beanConfig;
			this.userDetailsService = userDetailsService;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			super.configure(http);
			http
				.userDetailsService(userDetailsService).formLogin();
			http.apply(identityProvider())
				.configure(appConfig);
		}
	}

	@Configuration
	public static class AppSecurity extends WebSecurityConfigurerAdapter {

		private final BeanConfig beanConfig;
		private final CustomUserDetailsService userDetailsService;

		public AppSecurity(BeanConfig beanConfig,CustomUserDetailsService userDetailsService) {
			this.beanConfig = beanConfig;
			this.userDetailsService = userDetailsService;
		}
		
		@Bean
	    public PasswordEncoder passwordEncoder() {
	        return NoOpPasswordEncoder.getInstance();
	    }
		
		 @Autowired
		    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		    }

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/**")
				.authorizeRequests()
				.antMatchers("/**").authenticated()
				.and()
				.formLogin()
				//.successForwardUrl("/")

			;
		}
	}
}
