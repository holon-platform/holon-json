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
package com.holonplatform.json.jackson.internal.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.core.property.PropertyBox;

/**
 * {@link Feature} to configure Jackson object mapper with {@link PropertyBox} marshalling capabilities.
 *
 * @since 5.0.0
 */
public class JacksonConfigurationFeature implements Feature {

	/**
	 * Property name to put in jax-rs application configuration to disable jackson object mapper auto-configuration.
	 */
	public static final String DISABLE_JACKSON_AUTO_CONFIG = "holon.jackson.disable-autoconfig";

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		if (!context.getConfiguration().isRegistered(JacksonContextResolver.class)) {
			context.register(JacksonContextResolver.class);
		}
		return true;
	}

}
