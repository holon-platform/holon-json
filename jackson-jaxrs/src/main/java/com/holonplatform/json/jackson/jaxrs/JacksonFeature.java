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
package com.holonplatform.json.jackson.jaxrs;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.jaxrs.internal.JacksonConfigurationFeature;
import com.holonplatform.json.jackson.jaxrs.internal.JacksonProviderFeature;

/**
 * JAX-RS {@link Feature} to register Jackson {@link PropertyBox} JSON serializers/deserializers and context resolver
 * for properly configured {@link ObjectMapper}.
 * 
 * @since 5.0.0
 */
public class JacksonFeature implements Feature {

	/**
	 * Property name to put in JAX-RS application configuration to disable Jackson provider (message body reader and
	 * writer) and context resolver auto-configuration.
	 */
	public static final String JAXRS_DISABLE_JACKSON_AUTO_CONFIG = "holon.jackson.disable-autoconfig";

	/**
	 * Property name to put in JAX-RS application configuration to disable Jackson context resolver auto-configuration
	 * only (message body reader and writer will be auto-configured).
	 */
	public static final String JAXRS_DISABLE_JACKSON_CONTEXT_RESOLVER = "holon.jackson.disable-resolver";

	/**
	 * Property name to put in JAX-RS application configuration to enable JSON <em>pretty print</em> for the JSON
	 * message body writer.
	 */
	public static final String JAXRS_JSON_PRETTY_PRINT = "holon.jaxrs.json.pretty-print";

	/**
	 * Feature name
	 */
	public static final String FEATURE_NAME = JacksonFeature.class.getName();

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.core.Feature#configure(jakarta.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		// context resolver
		if (!context.getConfiguration().isRegistered(JacksonConfigurationFeature.class)) {
			context.register(JacksonConfigurationFeature.class);
		}
		// message body reader and writer
		if (!context.getConfiguration().isRegistered(JacksonProviderFeature.class)) {
			context.register(JacksonProviderFeature.class);
		}
		return true;
	}

}
