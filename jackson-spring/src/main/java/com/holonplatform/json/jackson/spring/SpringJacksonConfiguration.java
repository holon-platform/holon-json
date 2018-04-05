/*
 * Copyright 2016-2017 Axioma srl.
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

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;
import com.holonplatform.json.jackson.spring.internal.JacksonRestTemplateUtils;

/**
 * Utility interface to handle Jackson configuration in Spring environment.
 *
 * @since 5.0.0
 */
public interface SpringJacksonConfiguration {

	/**
	 * Configure Spring RestTemplate, setting up serializers and deserializers for {@link PropertyBox} type handling in
	 * Jackson HttpMessageConverters, if any. If no Jackson HttpMessageConverter is registered, a configured
	 * {@link MappingJackson2HttpMessageConverter} will be registered in RestTemplate.
	 * @param restTemplate RestTemplate to configure (not null)
	 * @return The configured RestTemplate instance
	 */
	public static RestTemplate configure(RestTemplate restTemplate) {
		ObjectUtils.argumentNotNull(restTemplate, "RestTemplate must be not null");
		JacksonConfiguration.configure(JacksonRestTemplateUtils.getJacksonConverter(restTemplate)
				.orElse(new MappingJackson2HttpMessageConverter()).getObjectMapper());
		return restTemplate;
	}

}
