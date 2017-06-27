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
package com.holonplatform.json.gson.internal.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.internal.GsonLogger;
import com.holonplatform.json.gson.jaxrs.GsonFeature;

/**
 * {@link Feature} to configure Gson with {@link PropertyBox} marshalling capabilities.
 *
 * @since 5.0.0
 */
public class GsonContextResolverFeature implements Feature {

	private final static Logger LOGGER = GsonLogger.create();

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		// check disabled
		if (context.getConfiguration().getProperties().containsKey(GsonFeature.JAXRS_DISABLE_GSON_AUTO_CONFIG)) {
			LOGGER.debug(() -> "Skip GsonContextResolver registration, [" + GsonFeature.JAXRS_DISABLE_GSON_AUTO_CONFIG
					+ "] property detected");
			return false;
		}
		if (context.getConfiguration().getProperties().containsKey(GsonFeature.JAXRS_DISABLE_GSON_CONTEXT_RESOLVER)) {
			LOGGER.debug(() -> "Skip GsonContextResolver registration, ["
					+ GsonFeature.JAXRS_DISABLE_GSON_CONTEXT_RESOLVER + "] property detected");
			return false;
		}
		// check pretty print
		boolean prettyPrint = false;
		if (context.getConfiguration().getProperties().containsKey(GsonFeature.JAXRS_JSON_PRETTY_PRINT)) {
			Object pp = context.getConfiguration().getProperties().getOrDefault(GsonFeature.JAXRS_JSON_PRETTY_PRINT,
					Boolean.FALSE);
			if (TypeUtils.isBoolean(pp.getClass()) && (boolean) pp) {
				prettyPrint = true;
			}
		}

		// register
		LOGGER.debug(() -> "<Runtime: " + context.getConfiguration().getRuntimeType()
				+ "> Registering ContextResolver [" + GsonContextResolver.class.getName() + "]");
		context.register(new GsonContextResolver(prettyPrint));
		return true;
	}

}
