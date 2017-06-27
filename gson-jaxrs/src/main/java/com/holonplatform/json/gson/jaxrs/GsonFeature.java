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
package com.holonplatform.json.gson.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.json.gson.internal.jaxrs.GsonContextResolverFeature;
import com.holonplatform.json.gson.internal.jaxrs.GsonProviderFeature;

/**
 * JAX-RS {@link Feature} to register Gson JSON providers and context resolver.
 * 
 * @since 5.0.0
 */
public class GsonFeature implements Feature {

	/**
	 * Property name to put in JAX-RS application configuration to disable Gson provider (message body reader and
	 * writer) and context resolver auto-configuration.
	 */
	public static final String JAXRS_DISABLE_GSON_AUTO_CONFIG = "holon.gson.disable-autoconfig";

	/**
	 * Property name to put in JAX-RS application configuration to disable Gson context resolver auto-configuration only
	 * (message body reader and writer will be auto-configured).
	 */
	public static final String JAXRS_DISABLE_GSON_CONTEXT_RESOLVER = "holon.gson.disable-resolver";

	/**
	 * Property name to put in JAX-RS application configuration to enable JSON <em>pretty print</em> for the JSON
	 * message body writer.
	 */
	public static final String JAXRS_JSON_PRETTY_PRINT = "holon.jaxrs.json.pretty-print";

	/**
	 * Feature name
	 */
	public static final String FEATURE_NAME = GsonFeature.class.getName();

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		// context resolver
		if (!context.getConfiguration().isRegistered(GsonContextResolverFeature.class)) {
			context.register(GsonContextResolverFeature.class);
		}
		// message body reader and writer
		if (!context.getConfiguration().isRegistered(GsonProviderFeature.class)) {
			context.register(GsonProviderFeature.class);
		}
		return true;
	}

}
