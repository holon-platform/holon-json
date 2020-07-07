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
package com.holonplatform.json.jackson.jaxrs.internal;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.json.internal.JsonLogger;
import com.holonplatform.json.jackson.jaxrs.JacksonFeature;

/**
 * {@link Feature} to register Jackson JSON providers.
 * 
 * @since 5.0.0
 */
public class JacksonProviderFeature implements Feature {

	private static final Logger LOGGER = JsonLogger.create();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		if (context.getConfiguration().getProperties().containsKey(JacksonFeature.JAXRS_DISABLE_JACKSON_AUTO_CONFIG)) {
			LOGGER.debug(() -> "Skip JacksonJsonProvider registration, ["
					+ JacksonFeature.JAXRS_DISABLE_JACKSON_AUTO_CONFIG + "] property detected");
			return false;
		}

		if (!context.getConfiguration().isRegistered(JacksonJsonPropertyBoxProvider.class)) {
			LOGGER.debug(() -> "<Runtime: " + context.getConfiguration().getRuntimeType() + "> Registering provider ["
					+ JacksonJsonPropertyBoxProvider.class.getName() + "]");
			context.register(JacksonJsonPropertyBoxProvider.class);
		}
		return true;

	}

}
