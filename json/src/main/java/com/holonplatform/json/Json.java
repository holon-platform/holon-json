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
package com.holonplatform.json;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Context;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.json.internal.JsonProviders;

/**
 * A simple API to serialize and deserialize Objects to and from JSON.
 * <p>
 * Any object supported by the concrete JSON parser implementation can be
 * serialized and deserialized.
 * </p>
 * <p>
 * A set of methods are specifically provided to deal with collections in a
 * simple way, serializing and deserializing to and from a JSON array. See for
 * example {@link #toJsonArray(Class, Collection)} or
 * {@link #fromJsonArray(JsonReader, Class)}.
 * </p>
 * <p>
 * The {@link JsonWriter} interface is used to provide the JSON serialization
 * result, allowing to obtain the JSON data in a number of ways, for example as
 * a String, as a byte array or writing it into a provided writer.
 * </p>
 * <p>
 * At the opposite, the {@link JsonReader} interface is used to represent the
 * JSON data source which has to be deserialized into a Java object, and
 * provides a number of static builder methods to obtain the JSON data from
 * different sources, for example a String, an array of bytes or an
 * {@link InputStream}.
 * </p>
 * <p>
 * This API makes available a specific set of convenience methods to directly
 * support the Holon platform {@link PropertyBox} data class. A
 * {@link PropertyBox} is serialized as a generic JSON object, using the
 * property names as the object attribute names. Any {@link VirtualProperty} is
 * ignored by default. In the deserialization phase, it is necessary to provide
 * the {@link Property} set to use to create a {@link PropertyBox} instance form
 * a JSON object. For this reason, methods like
 * {@link #fromJson(JsonReader, Iterable)} allow to provide a {@link Property}
 * set for {@link PropertyBox} deserialization.
 * </p>
 * <p>
 * The {@link JsonProvider} interface can be used to provide a Json API
 * implementation using the default Java service extension feature. The default
 * Json API implementation can be obtained using tge {@link #get()} or
 * {@link #require()} methods. See {@link JsonProvider} JavaDocs and methods
 * documentation for further information.
 * </p>
 * <p>
 * The Holon platform JSON module provides two standard implementations of this
 * API by default: one based on the <em>Jackson</em> library and one based on
 * the <em>Gson</em> library. See the Holon platform documentation for details.
 * </p>
 * 
 * @since 5.1.0
 */
public interface Json {

	/**
	 * Default {@link Context} resource reference
	 */
	public static final String CONTEXT_KEY = Json.class.getName();

	/**
	 * Serialize given <code>value</code> to JSON.
	 * @param value Value to serialize
	 * @return a {@link JsonWriter} from which to obtain the serialized JSON data.
	 */
	JsonWriter toJson(Object value);

	/**
	 * Serialize given <code>value</code> to a JSON string.
	 * @param value Value to serialize
	 * @return the JSON representation of the value as a String, <code>null</code>
	 *         if given value was <code>null</code>
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	default String toJsonString(Object value) {
		return toJson(value).asString();
	}

	/**
	 * Serialize given collection of values as a JSON array.
	 * @param <T>    Values type
	 * @param type   Value type
	 * @param values Values collection
	 * @return a {@link JsonWriter} from which to obtain the serialized JSON data.
	 */
	<T> JsonWriter toJsonArray(Class<T> type, Collection<T> values);

	/**
	 * Serialize given array of values as a JSON array.
	 * @param <T>    Values type
	 * @param type   Value type
	 * @param values Values to serialize
	 * @return a {@link JsonWriter} from which to obtain the serialized JSON data.
	 */
	@SuppressWarnings("unchecked")
	default <T> JsonWriter toJsonArray(Class<T> type, T... values) {
		return toJsonArray(type, (values == null) ? null : Arrays.asList(values));
	}

	/**
	 * Serialize given collection of values as a JSON array string.
	 * @param <T>    Values type
	 * @param type   Value type
	 * @param values Values to serialize
	 * @return the JSON array as a String
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	default <T> String toJsonArrayString(Class<T> type, Collection<T> values) {
		return toJsonArray(type, values).toString();
	}

	/**
	 * Serialize given array of values as a JSON array string.
	 * @param <T>    Values type
	 * @param type   Value type
	 * @param values Values to serialize
	 * @return the JSON array as a String
	 * @throws JsonWriteException If a JSON serialization error occured
	 */
	@SuppressWarnings("unchecked")
	default <T> String toJsonArrayString(Class<T> type, T... values) {
		return toJsonArray(type, values).toString();
	}

	/**
	 * Deserializes the specified JSON source into an object of the specified type.
	 * @param <T>    desired object type
	 * @param reader JSON data source (not null)
	 * @param type   the type of the desired object (not null)
	 * @return the deserialized object instance
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	<T> T fromJson(JsonReader reader, Class<T> type);

	/**
	 * Deserializes the specified JSON string into an object of the specified type.
	 * @param <T>  desired object type
	 * @param json JSON string
	 * @param type the type of the desired object (not null)
	 * @return the deserialized object instance
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	default <T> T fromJson(String json, Class<T> type) {
		return fromJson(JsonReader.from(json), type);
	}

	/**
	 * Deserializes the specified JSON array data source into a {@link List} of
	 * objects of the specified type.
	 * @param <T>    desired object type
	 * @param reader JSON data source (not null)
	 * @param type   the type of the desired objects (not null)
	 * @return the deserialized objects list
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	<T> List<T> fromJsonArray(JsonReader reader, Class<T> type);

	/**
	 * Deserializes the specified JSON array string into a {@link List} of objects
	 * of the specified type.
	 * @param <T>  desired object type
	 * @param json JSON array string
	 * @param type the type of the desired objects (not null)
	 * @return the deserialized objects list
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	default <T> List<T> fromJsonArray(String json, Class<T> type) {
		return fromJsonArray(JsonReader.from(json), type);
	}

	/**
	 * Deserializes the specified JSON data source into a {@link PropertyBox}, using
	 * given <code>propertySet</code> as {@link PropertyBox} property set.
	 * @param <P>         Actual property type
	 * @param reader      JSON data source (not null)
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox} (not null)
	 * @return the deserialized {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	<P extends Property> PropertyBox fromJson(JsonReader reader, Iterable<P> propertySet);

	/**
	 * Deserializes the specified JSON data source into a {@link PropertyBox}, using
	 * given <code>propertySet</code> as {@link PropertyBox} property set.
	 * @param reader      JSON data source (not null)
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox} (not null)
	 * @return the deserialized {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	default PropertyBox fromJson(JsonReader reader, Property... propertySet) {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		return fromJson(reader, PropertySet.of(propertySet));
	}

	/**
	 * Deserializes the specified JSON string into a {@link PropertyBox}, using
	 * given <code>propertySet</code> as {@link PropertyBox} property set.
	 * @param <P>         Actual property type
	 * @param json        JSON string
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox} (not null)
	 * @return the deserialized {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> PropertyBox fromJson(String json, Iterable<P> propertySet) {
		return fromJson(JsonReader.from(json), propertySet);
	}

	/**
	 * Deserializes the specified JSON string into a {@link PropertyBox}, using
	 * given <code>propertySet</code> as {@link PropertyBox} property set.
	 * @param json        JSON string
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox} (not null)
	 * @return the deserialized {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	default PropertyBox fromJson(String json, Property... propertySet) {
		return fromJson(JsonReader.from(json), propertySet);
	}

	/**
	 * Deserializes the specified JSON array data source into a list of
	 * {@link PropertyBox}, using given <code>propertySet</code> as
	 * {@link PropertyBox} property set.
	 * @param <P>         Actual property type
	 * @param reader      JSON data source (not null)
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox}s (not null)
	 * @return the deserialized {@link List} of {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	<P extends Property> List<PropertyBox> fromJsonArray(JsonReader reader, Iterable<P> propertySet);

	/**
	 * Deserializes the specified JSON array data source into a list of
	 * {@link PropertyBox}, using given <code>propertySet</code> as
	 * {@link PropertyBox} property set.
	 * @param reader      JSON data source (not null)
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox}s (not null)
	 * @return the deserialized {@link List} of {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	default List<PropertyBox> fromJsonArray(JsonReader reader, Property... propertySet) {
		return fromJsonArray(reader, PropertySet.of(propertySet));
	}

	/**
	 * Deserializes the specified JSON array string into a list of
	 * {@link PropertyBox}, using given <code>propertySet</code> as
	 * {@link PropertyBox} property set.
	 * @param <P>         Actual property type
	 * @param json        JSON string
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox}s (not null)
	 * @return the deserialized {@link List} of {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> List<PropertyBox> fromJsonArray(String json, Iterable<P> propertySet) {
		return fromJsonArray(JsonReader.from(json), PropertySet.of(propertySet));
	}

	/**
	 * Deserializes the specified JSON array string into a list of
	 * {@link PropertyBox}, using given <code>propertySet</code> as
	 * {@link PropertyBox} property set.
	 * @param json        JSON string
	 * @param propertySet Property set to use to build the deserialized
	 *                    {@link PropertyBox}s (not null)
	 * @return the deserialized {@link List} of {@link PropertyBox}
	 * @throws JsonReadException If a JSON deserialization error occured
	 */
	@SuppressWarnings("rawtypes")
	default List<PropertyBox> fromJsonArray(String json, Property... propertySet) {
		return fromJsonArray(JsonReader.from(json), PropertySet.of(propertySet));
	}

	// ------- Providers

	/**
	 * Requires a {@link Json} implementation, either from {@link Context}, if
	 * available using {@link #CONTEXT_KEY}, or relying on registered
	 * {@link JsonProvider}s and using the one with higher priority.
	 * <p>
	 * If not available using {@link #get()}, an {@link IllegalStateException} is
	 * thrown.
	 * </p>
	 * @throws IllegalStateException If a {@link Json} implementation is not
	 *                               available
	 * @return The {@link Json} implementation
	 */
	static Json require() {
		return get().orElseThrow(() -> new IllegalStateException("No Json implementation available"));
	}

	/**
	 * Try to obtain a {@link Json} implementation, either from {@link Context}, if
	 * available using {@link #CONTEXT_KEY}, or relying on registered
	 * {@link JsonProvider}s and using the one with higher priority.
	 * @return The {@link Json} implementation, if available
	 */
	static Optional<Json> get() {
		return get(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Try to obtain a {@link Json} implementation using given Classloader, either
	 * from {@link Context}, if available using {@link #CONTEXT_KEY}, or relying on
	 * registered {@link JsonProvider}s and using the one with higher priority.
	 * @param classLoader ClassLoader to use
	 * @return The {@link Json} implementation, if available
	 */
	static Optional<Json> get(ClassLoader classLoader) {
		// check in context
		Optional<Json> fromContext = Context.get().resource(CONTEXT_KEY, Json.class, classLoader);
		if (fromContext.isPresent()) {
			return fromContext;
		}
		// use providers
		return JsonProviders.getDefaultJsonProvider(classLoader).map(JsonProvider::provide);
	}

	// ------- Exceptions

	/**
	 * Base Json exception class.
	 */
	public abstract class JsonException extends RuntimeException {

		private static final long serialVersionUID = 4255245974256602343L;

		/**
		 * Constructor with error message.
		 * @param message Error message
		 */
		public JsonException(String message) {
			super(message);
		}

		/**
		 * Constructor with error message and cause.
		 * @param message Error message
		 * @param cause   Cause
		 */
		public JsonException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	/**
	 * Exception thrown for JSON deserialization errors.
	 */
	public class JsonReadException extends JsonException {

		private static final long serialVersionUID = 2962843395438600558L;

		/**
		 * Constructor with error message.
		 * @param message Error message
		 */
		public JsonReadException(String message) {
			super(message);
		}

		/**
		 * Constructor with error message and cause.
		 * @param message Error message
		 * @param cause   Cause
		 */
		public JsonReadException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	/**
	 * Exception thrown for JSON serialization errors.
	 */
	public class JsonWriteException extends JsonException {

		private static final long serialVersionUID = -3108117832731735617L;

		/**
		 * Constructor with error message.
		 * @param message Error message
		 */
		public JsonWriteException(String message) {
			super(message);
		}

		/**
		 * Constructor with error message and cause.
		 * @param message Error message
		 * @param cause   Cause
		 */
		public JsonWriteException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
