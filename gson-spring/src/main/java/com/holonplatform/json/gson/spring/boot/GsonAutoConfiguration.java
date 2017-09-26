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
package com.holonplatform.json.gson.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.GsonConfiguration;

/**
 * Spring boot auto-configuration to configure {@link Gson} bean, registering serializers and deserializers for
 * {@link PropertyBox} type handling.
 * 
 * @since 5.0.0
 */
@Configuration
@ConditionalOnClass(Gson.class)
@AutoConfigureBefore(org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class)
public class GsonAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Gson gson() {
		return GsonConfiguration.builder().create();
	}

}
