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
package com.holonplatform.json.internal.model;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.config.PropertyBoxSerializationMode;
import com.holonplatform.json.model.JsonPropertySetSerializationTreeResolver;
import com.holonplatform.json.model.PropertySetSerializationTree;
import com.holonplatform.json.model.PropertySetSerializationTreeResolver;

/**
 * Default JSON {@link PropertySetSerializationTreeResolver}.
 *
 * @since 5.2.0
 */
public enum DefaultJsonPropertySetSerializationTreeResolver implements JsonPropertySetSerializationTreeResolver {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	private final static Predicate<Property<?>> PATH_VALIDATOR = property -> {
		return Path.class.isAssignableFrom(property.getClass());
	};

	/**
	 * PATH mode resolver
	 */
	private final static PropertySetSerializationTreeResolver PATH_MODE_RESOLVER = PropertySetSerializationTreeResolver
			.builder().validator(PATH_VALIDATOR).build();

	/**
	 * ALL mode resolver
	 */
	private final static PropertySetSerializationTreeResolver ALL_MODE_RESOLVER = PropertySetSerializationTreeResolver
			.builder().validator(p -> true).build();

	/**
	 * PATH mode cache
	 */
	private final static Map<PropertySet<?>, PropertySetSerializationTree> PATH_CACHE = new WeakHashMap<>();

	/**
	 * ALL mode cache
	 */
	private final static Map<PropertySet<?>, PropertySetSerializationTree> ALL_CACHE = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.json.model.JsonPropertySetSerializationTreeResolver#resolve(com.holonplatform.core.property.
	 * PropertySet, com.holonplatform.json.config.PropertyBoxSerializationMode)
	 */
	@Override
	public PropertySetSerializationTree resolve(PropertySet<?> propertySet,
			PropertyBoxSerializationMode serializationMode) {

		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");

		final PropertyBoxSerializationMode mode = (serializationMode != null) ? serializationMode
				: PropertyBoxSerializationMode.getDefault();

		switch (mode) {
		case ALL:
			return ALL_CACHE.computeIfAbsent(propertySet, ps -> ALL_MODE_RESOLVER.resolve(ps));
		case PATH:
		default:
			return PATH_CACHE.computeIfAbsent(propertySet, ps -> PATH_MODE_RESOLVER.resolve(ps));
		}
	}

}
