/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.json.gson.spring;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;
import com.holonplatform.json.gson.spring.internal.GsonRestTemplateUtils;

/**
 * Utility interface to handle Gson configuration in Spring environment.
 *
 * @since 5.0.0
 */
public interface SpringGsonConfiguration {

	/**
	 * Configure Spring RestTemplate, setting up serializers and deserializers for {@link PropertyBox} type handling in
	 * Gson HttpMessageConverters, if any. If no Jackson Gson is registered, a {@link GsonHttpMessageConverter} with a
	 * configured Gson instance will be registered in RestTemplate.
	 * <p>
	 * In order to this method to work, <code>spring-web</code> artifact must be present in classpath.
	 * </p>
	 * @param restTemplate RestTemplate to configure (not null)
	 * @return The configured RestTemplate instance
	 */
	public static RestTemplate configure(RestTemplate restTemplate) {
		ObjectUtils.argumentNotNull(restTemplate, "RestTemplate must be not null");
		GsonRestTemplateUtils.getGsonConverter(restTemplate).orElse(new GsonHttpMessageConverter())
				.setGson(GsonConfiguration.builder().create());
		return restTemplate;
	}

}
