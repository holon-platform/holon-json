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
package com.holonplatform.json.gson.internal;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;

/**
 * Gson serializer to handle {@link PropertyBox} serialization.
 * 
 * @since 5.0.0
 */
public class GsonPropertyBoxSerializer implements JsonSerializer<PropertyBox> {

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
	 * com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(PropertyBox src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject obj = new JsonObject();

		for (Property<?> property : src) {
			// only model properties are serialized
			if (property instanceof Path) {
				final Object value = src.getValue(property);
				// do not serialize null values
				if (value != null) {
					obj.add(((Path<?>) property).getName(), context.serialize(value));
				}
			}
		}

		return obj;
	}

}
