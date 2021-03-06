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
package com.holonplatform.json.gson.jaxrs.internal;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.json.gson.jaxrs.GsonFeature;
import com.holonplatform.json.internal.JsonLogger;

/**
 * {@link Feature} to register Gson JSON providers.
 * 
 * @since 5.0.0
 */
public class GsonProviderFeature implements Feature {

	private static final Logger LOGGER = JsonLogger.create();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		if (context.getConfiguration().getProperties().containsKey(GsonFeature.JAXRS_DISABLE_GSON_AUTO_CONFIG)) {
			LOGGER.debug(() -> "Skip GsonJsonProvider registration, [" + GsonFeature.JAXRS_DISABLE_GSON_AUTO_CONFIG
					+ "] property detected");
			return false;
		}
		if (!context.getConfiguration().isRegistered(GsonJsonProvider.class)) {
			LOGGER.debug(() -> "<Runtime: " + context.getConfiguration().getRuntimeType() + "> Registering provider ["
					+ GsonJsonProvider.class.getName() + "]");
			context.register(GsonJsonProvider.class);
		}
		return true;
	}

}
