/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.json.model;

import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.internal.model.DefaultJsonPropertySetSerializationTreeResolver;

/**
 * A {@link PropertySetSerializationTreeResolver} with {@link PropertyBoxSerializationMode} support.
 *
 * @since 5.2.0
 */
public interface JsonPropertySetSerializationTreeResolver {

	/**
	 * Resolve the {@link PropertySetSerializationTree} if given {@link PropertySet} definition.
	 * @param propertySet The property set for which to obtain the serialization tree (not null)
	 * @param serializationMode PropertyBox serialization mode
	 * @return The resolved {@link PropertySetSerializationTree}
	 */
	PropertySetSerializationTree resolve(PropertySet<?> propertySet, PropertyBoxSerializationMode serializationMode);

	/**
	 * Get the default {@link JsonPropertySetSerializationTreeResolver}.
	 * @return The default {@link JsonPropertySetSerializationTreeResolver}
	 */
	static JsonPropertySetSerializationTreeResolver getDefault() {
		return DefaultJsonPropertySetSerializationTreeResolver.INSTANCE;
	}

}
