/*
 * Copyright 2014-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ufo.cinemasystem;

import org.salespointframework.EnableSalespoint;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * The main application class.
 */
@EnableSalespoint
public class Application {

	/**
	 * The main application method
	 * 
	 * @param args application arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
        
        /**
         * This logger object is kept here so we can turn off the annoying 
         * " Using default formatter for toString()" spam from moneta without needing a 
         * logging.properties.
         * 
         * This is fixed in https://github.com/JavaMoney/jsr354-ri/issues/361 upstream,
         * but I'm not sure if we can upgrade moneta without breaking salespoint.
         */
        private static final java.util.logging.Logger monetaSilencer;
        
        static {
            monetaSilencer = java.util.logging.Logger.getLogger(org.javamoney.moneta.Money.class.getName());
            monetaSilencer.setLevel(java.util.logging.Level.WARNING);
            monetaSilencer.setUseParentHandlers(false);
        }

	@Configuration
	static class WebSecurityConfiguration {
		@Bean
		public BCryptPasswordEncoder pwEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		SecurityFilterChain videoShopSecurity(HttpSecurity http) throws Exception {

			return http
					.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
					.csrf(csrf -> csrf.disable())
					.formLogin(login -> login.loginProcessingUrl("/login"))
					.logout(logout -> logout.logoutUrl("/lunar_space_port/logOut").logoutSuccessUrl("/"))
					.build();
		}
	}
}
