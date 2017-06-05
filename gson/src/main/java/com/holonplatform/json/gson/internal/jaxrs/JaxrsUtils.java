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

import java.io.Serializable;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;

import com.holonplatform.core.internal.utils.ClassUtils;

/**
 * Utility class for JAX-RS operations and detection.
 * 
 * @since 5.0.0
 */
public final class JaxrsUtils implements Serializable {

	private static final long serialVersionUID = -3608716547640711144L;

	/**
	 * Whether Jersey is available from classpath of current ClassLoader
	 */
	public static final boolean JERSEY_PRESENT = ClassUtils.isPresent("org.glassfish.jersey.CommonProperties",
			ClassUtils.getDefaultClassLoader());

	private JaxrsUtils() {
	}

	public static boolean registerFeature(FeatureContext context, Class<? extends Feature> feature, String featureName,
			String featurePropertyName) {
		return JERSEY_PRESENT ? registerJerseyFeature(context, feature, featureName, featurePropertyName)
				: registerStandardFeature(context, feature);
	}

	private static boolean registerStandardFeature(FeatureContext context, Class<? extends Feature> feature) {
		if (!context.getConfiguration().isRegistered(feature)) {
			context.register(feature);
		}
		return true;
	}

	private static boolean registerJerseyFeature(FeatureContext context, Class<? extends Feature> feature,
			String featureName, String featurePropertyName) {
		final Configuration config = context.getConfiguration();

		final String jsonFeature = CommonProperties.getValue(config.getProperties(), config.getRuntimeType(),
				featurePropertyName, featureName, String.class);
		// Other providers registered.
		if (!featureName.equalsIgnoreCase(jsonFeature)) {
			return false;
		}

		// Disable other providers.
		context.property(PropertiesHelper.getPropertyNameForRuntime(featurePropertyName, config.getRuntimeType()),
				featureName);

		// Register
		if (!config.isRegistered(feature)) {
			context.register(feature);
		}
		return true;
	}

}
