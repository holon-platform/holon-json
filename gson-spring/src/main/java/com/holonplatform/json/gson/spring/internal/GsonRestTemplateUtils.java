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
package com.holonplatform.json.gson.spring.internal;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Utility class to manage Gson {@link RestTemplate} configuration.
 *
 * @since 5.1.0
 */
public final class GsonRestTemplateUtils implements Serializable {

	private static final long serialVersionUID = 6300505670916957351L;

	private GsonRestTemplateUtils() {
	}

	/**
	 * Get a registered GsonHttpMessageConverter from RestTemplate
	 * @param restTemplate RestTemplate
	 * @return Optional GsonHttpMessageConverter, empty if not registered
	 */
	public static Optional<GsonHttpMessageConverter> getGsonConverter(RestTemplate restTemplate) {
		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (GsonHttpMessageConverter.class.isAssignableFrom(converter.getClass())) {
				return Optional.of((GsonHttpMessageConverter) converter);
			}
		}
		return Optional.empty();
	}

}
