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

import java.util.function.Function;
import java.util.function.Predicate;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.internal.model.DefaultPropertySetSerializationTreeResolver;

/**
 * Resolver to obtain a {@link PropertySetSerializationTree} from a {@link PropertySet} definition.
 *
 * @since 5.2.0
 */
public interface PropertySetSerializationTreeResolver {

	/**
	 * Resolve the {@link PropertySetSerializationTree} if given {@link PropertySet} definition.
	 * @param propertySet The property set for which to obtain the serialization tree (not null)
	 * @return The resolved {@link PropertySetSerializationTree}
	 */
	PropertySetSerializationTree resolve(PropertySet<?> propertySet);

	/**
	 * Return a builder to build {@link PropertySetSerializationTreeResolver} instances.
	 * @return A new {@link PropertySetSerializationTreeResolver} builder
	 */
	static Builder builder() {
		return new DefaultPropertySetSerializationTreeResolver.DefaultBuilder();
	}

	/**
	 * {@link PropertySetSerializationTreeResolver} builder.
	 */
	public interface Builder {

		/**
		 * Set the property set pre-processor.
		 * @param preProcessor Property set pre-processor (not null)
		 * @return this
		 */
		@SuppressWarnings("rawtypes")
		Builder preProcessor(Function<PropertySet<?>, Iterable<Property>> preProcessor);

		/**
		 * Set the property set property validator.
		 * @param validator Property validator (not null)
		 * @return this
		 */
		Builder validator(Predicate<Property<?>> validator);

		/**
		 * Build the {@link PropertySetSerializationTreeResolver}.
		 * @return A new {@link PropertySetSerializationTreeResolver} instance
		 */
		PropertySetSerializationTreeResolver build();

	}

}
