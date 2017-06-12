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
package com.holonplatform.json.gson.internal.jaxrs.jersey;

import javax.annotation.Priority;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.json.gson.internal.GsonLogger;
import com.holonplatform.json.gson.internal.jaxrs.GsonConfigurationFeature;

/**
 * {@link AutoDiscoverable} class to configure Gson with {@link PropertyBox} marshalling capabilities.
 *
 * @since 5.0.0
 */
@Priority(AutoDiscoverable.DEFAULT_PRIORITY + 10000)
public class GsonConfigurationAutoDiscoverable implements AutoDiscoverable {

	private final static Logger LOGGER = GsonLogger.create();

	/*
	 * (non-Javadoc)
	 * @see org.glassfish.jersey.internal.spi.AutoDiscoverable#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public void configure(FeatureContext context) {
		if (!context.getConfiguration().isRegistered(GsonConfigurationFeature.class)) {
			if (!context.getConfiguration().getProperties()
					.containsKey(GsonConfigurationFeature.DISABLE_GSON_AUTO_CONFIG)) {
				LOGGER.debug(() -> "GsonConfigurationAutoDiscoverable: registering GsonConfigurationFeature");
				context.register(GsonConfigurationFeature.class);
			} else {
				LOGGER.debug(() -> "GsonConfigurationAutoDiscoverable: skip GsonConfigurationFeature registration, ["
						+ GsonConfigurationFeature.DISABLE_GSON_AUTO_CONFIG + "] property detected");
			}
		}
	}

}
