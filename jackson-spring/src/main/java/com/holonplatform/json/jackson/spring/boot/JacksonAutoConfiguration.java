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
package com.holonplatform.json.jackson.spring.boot;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.JacksonConfiguration;

/**
 * Spring boot auto-configuration to configure Jackson {@link ObjectMapper} bean, registering serializers and
 * deserializers for {@link PropertyBox} type handling.
 * 
 * @since 5.0.0
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnBean(ObjectMapper.class)
@AutoConfigureAfter(org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class)
public class JacksonAutoConfiguration {

	@Bean
	public static ObjectMapperPostProcessor jacksonObjectMapperPostProcessor() {
		return new ObjectMapperPostProcessor();
	}

	private static final class ObjectMapperPostProcessor implements BeanPostProcessor {

		/*
		 * (non-Javadoc)
		 * @see
		 * org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
		 * java.lang.String)
		 */
		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
			return bean;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
		 * java.lang.String)
		 */
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if (ObjectMapper.class.isAssignableFrom(bean.getClass())) {
				JacksonConfiguration.configure((ObjectMapper) bean);
			}
			return bean;
		}

	}

}
