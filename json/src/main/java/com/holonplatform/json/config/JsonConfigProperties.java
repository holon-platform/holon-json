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
package com.holonplatform.json.config;

import com.holonplatform.core.config.ConfigProperty;
import com.holonplatform.core.config.ConfigPropertySet;
import com.holonplatform.core.datastore.DatastoreConfigProperties;
import com.holonplatform.core.internal.config.DefaultConfigPropertySet;
import com.holonplatform.core.property.PropertyBox;

/**
 * A {@link ConfigPropertySet} for JSON serialization and deserialization configuration, using {@link #DEFAULT_NAME} as
 * property prefix.
 *
 * @since 5.0.0
 */
public interface JsonConfigProperties extends ConfigPropertySet {

	/**
	 * Configuration property set default name
	 */
	static final String DEFAULT_NAME = "holon.json";

	/**
	 * {@link PropertyBox} serialization mode. Default is {@link PropertyBoxSerializationMode#getDefault()}.
	 */
	static final ConfigProperty<PropertyBoxSerializationMode> PROPERTYBOX_SERIALIZATION_MODE = ConfigProperty
			.create("propertyBox.serialization.mode", PropertyBoxSerializationMode.class);

	/**
	 * PROPERTYBOX_SERIALIZATION_MODE configuration property complete name
	 */
	static final String PROPERTYBOX_SERIALIZATION_MODE_ATTRIBUTE_NAME = JsonConfigProperties.DEFAULT_NAME + "."
			+ PROPERTYBOX_SERIALIZATION_MODE.getKey();

	/**
	 * Builder to create property set instances bound to a property data source.
	 * @return ConfigPropertySet builder
	 */
	static Builder<DatastoreConfigProperties> builder() {
		return new DefaultConfigPropertySet.DefaultBuilder<>(new JsonConfigPropertiesImpl());
	}

	/**
	 * Default implementation
	 */
	static class JsonConfigPropertiesImpl extends DefaultConfigPropertySet implements JsonConfigProperties {

		public JsonConfigPropertiesImpl() {
			super(DEFAULT_NAME);
		}

	}

}
