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
package com.holonplatform.json.jackson.internal.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.jackson.internal.JacksonLogger;
import com.holonplatform.json.jackson.jaxrs.JacksonFeature;

/**
 * {@link Feature} to configure Jackson object mapper with {@link PropertyBox} marshalling capabilities.
 *
 * @since 5.0.0
 */
public class JacksonConfigurationFeature implements Feature {

	private final static Logger LOGGER = JacksonLogger.create();

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		if (context.getConfiguration().getProperties()
				.containsKey(JacksonFeature.JAXRS_DISABLE_JACKSON_CONTEXT_RESOLVER)) {
			LOGGER.debug(() -> "Skip JacksonContextResolver registration, ["
					+ JacksonFeature.JAXRS_DISABLE_JACKSON_CONTEXT_RESOLVER + "] property detected");
			return false;
		}
		
		if (!context.getConfiguration().isRegistered(JacksonContextResolver.class)) {

			// check pretty print
			boolean prettyPrint = false;
			if (context.getConfiguration().getProperties().containsKey(JacksonFeature.JAXRS_JSON_PRETTY_PRINT)) {
				Object pp = context.getConfiguration().getProperties()
						.getOrDefault(JacksonFeature.JAXRS_JSON_PRETTY_PRINT, Boolean.FALSE);
				if (TypeUtils.isBoolean(pp.getClass()) && (boolean) pp) {
					prettyPrint = true;
				}
			}

			// register
			LOGGER.debug(() -> "<Runtime: " + context.getConfiguration().getRuntimeType()
					+ "> Registering ContextResolver [" + JacksonContextResolver.class.getName() + "]");
			context.register(new JacksonContextResolver(prettyPrint));
		}
		return true;
	}

}
