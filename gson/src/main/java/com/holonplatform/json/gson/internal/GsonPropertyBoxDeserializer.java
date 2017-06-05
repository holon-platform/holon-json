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
package com.holonplatform.json.gson.internal;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.holonplatform.core.Context;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;

/**
 * Gson deserializer to handle {@link PropertyBox} deserialization.
 * 
 * @since 5.0.0
 */
public class GsonPropertyBoxDeserializer implements JsonDeserializer<PropertyBox> {

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type,
	 * com.google.gson.JsonDeserializationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		// get property set
		final PropertySet<?> propertySet = Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class)
				.orElseThrow(() -> new JsonParseException("Missing PropertySet instance to build a PropertyBox. "
						+ "A PropertySet instance must be available as context resource to perform PropertyBox deserialization."));

		// get json object
		final JsonObject obj = json.getAsJsonObject();

		// property box
		PropertyBox.Builder builder = PropertyBox.builder(propertySet).invalidAllowed(true);
		propertySet.forEach(p -> {
			getPathName(p).ifPresent(n -> {
				final Object value = obj.get(n) != null ? context.deserialize(obj.get(n), p.getType()) : null;
				if (value != null) {
					builder.setIgnoreReadOnly(p, value);
				}
			});
		});
		return builder.build();
	}

	/**
	 * If given property is a {@link Path}, returns the path name.
	 * @param property Property (must be not null)
	 * @return The path name if given property is a {@link Path}, an empty Optional otherwise.
	 */
	private static Optional<String> getPathName(Property<?> property) {
		if (property instanceof Path) {
			return Optional.ofNullable(((Path<?>) property).getName());
		}
		return Optional.empty();
	}

}
