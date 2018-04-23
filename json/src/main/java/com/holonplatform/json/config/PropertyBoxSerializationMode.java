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

import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;

/**
 * Enumeration of {@link PropertyBox} property serialization mode when it is serialized as a JSON object.
 *
 * @since 5.1.0
 * 
 * @see JsonConfigProperties
 */
public enum PropertyBoxSerializationMode {

	/**
	 * Serialize only the {@link Path} type properties.
	 * <p>
	 * The path name, obtained through {@link Path#getName()}, is used as serialized property name.
	 * </p>
	 * <p>
	 * This is the default serialization mode.
	 * </p>
	 */
	PATH,

	/**
	 * Serialize all the properties.
	 * <p>
	 * The property name, obtained through {@link Property#getName()}, is used as serialized property name.
	 * </p>
	 */
	ALL;

	/**
	 * Get the default {@link PropertyBoxSerializationMode}.
	 * @return The default {@link PropertyBoxSerializationMode}
	 */
	public static PropertyBoxSerializationMode getDefault() {
		return PATH;
	}

}
