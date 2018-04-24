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
package com.holonplatform.json.gson.jaxrs.internal.jersey;

import javax.annotation.Priority;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.util.PropertiesHelper;

import com.holonplatform.json.gson.jaxrs.GsonFeature;

/**
 * {@link AutoDiscoverable} registering {@link GsonFeature} if it is not already registered.
 *
 * @since 5.0.0
 */
@Priority(AutoDiscoverable.DEFAULT_PRIORITY - 100)
public class GsonAutoDiscoverable implements AutoDiscoverable {

	private static final String JERSEY_JSON_PROVIDER_PROPERTY = "jersey.config.jsonFeature";

	/*
	 * (non-Javadoc)
	 * @see org.glassfish.jersey.internal.spi.AutoDiscoverable#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public void configure(FeatureContext context) {
		registerJerseyJsonFeature(context, GsonFeature.class, GsonFeature.FEATURE_NAME);
	}

	/**
	 * Register a Jersey JSON provider feature only if another JSON provider is not already registered, checking
	 * {@link #JERSEY_JSON_PROVIDER_PROPERTY} property value.
	 * @param context Feature context
	 * @param feature Feature to register
	 * @param featureName Feature name to register
	 * @return <code>true</code> if feature was registered, <code>false</code> otherwise
	 */
	private static boolean registerJerseyJsonFeature(FeatureContext context, Class<? extends Feature> feature,
			String featureName) {
		final Configuration config = context.getConfiguration();

		final String jsonFeature = CommonProperties.getValue(config.getProperties(), config.getRuntimeType(),
				JERSEY_JSON_PROVIDER_PROPERTY, featureName, String.class);
		if (!featureName.equalsIgnoreCase(jsonFeature)) {
			// Other JSON providers registered
			return false;
		}
		// Disable other JSON providers
		context.property(
				PropertiesHelper.getPropertyNameForRuntime(JERSEY_JSON_PROVIDER_PROPERTY, config.getRuntimeType()),
				featureName);
		// Register
		if (!config.isRegistered(feature)) {
			context.register(feature);
		}
		return true;
	}

}
