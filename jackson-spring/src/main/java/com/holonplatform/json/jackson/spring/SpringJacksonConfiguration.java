/*
 * Copyright 2000-2016 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.json.jackson.spring;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;

/**
 * Utility class to handle Jackson configuration for Spring.
 *
 * @since 5.0.0
 */
public final class SpringJacksonConfiguration implements Serializable {

	private static final long serialVersionUID = 1337470063296589625L;

	private SpringJacksonConfiguration() {
	}
	
	/**
	 * Configure Spring RestTemplate, setting up serializers and deserializers for {@link PropertyBox} type handling in
	 * Jackson HttpMessageConverters, if any. If no Jackson HttpMessageConverter is registered, a configured
	 * {@link MappingJackson2HttpMessageConverter} will be registered in RestTemplate.
	 * <p>
	 * In order to this method to work, <code>spring-web</code> artifact must be present in classpath.
	 * </p>
	 * @param restTemplate RestTemplate to configure
	 */
	public static void configure(RestTemplate restTemplate) {
		JacksonConfiguration.configure(
				getJacksonConverter(restTemplate).orElse(new MappingJackson2HttpMessageConverter()).getObjectMapper());

	}

	/**
	 * Get a registered AbstractJackson2HttpMessageConverter from RestTemplate
	 * @param restTemplate RestTemplate
	 * @return Optional AbstractJackson2HttpMessageConverter, empty if not registered
	 */
	private static Optional<AbstractJackson2HttpMessageConverter> getJacksonConverter(RestTemplate restTemplate) {
		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converter.getClass())) {
				return Optional.of((AbstractJackson2HttpMessageConverter) converter);
			}
		}
		return Optional.empty();
	}

}
