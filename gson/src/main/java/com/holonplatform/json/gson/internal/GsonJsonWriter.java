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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.json.Json.JsonWriteException;
import com.holonplatform.json.JsonWriter;

/**
 * Gson implementation of the {@link JsonWriter}.
 *
 * @since 5.1.0
 */
public class GsonJsonWriter implements JsonWriter {

	private final Gson gson;
	private final Object value;
	private final Type typeOfSrc;

	/**
	 * Default constructor.
	 * @param gson Gson istance (not null)
	 * @param value Value to serialize
	 */
	public GsonJsonWriter(Gson gson, Object value) {
		this(gson, value, null);
	}

	/**
	 * Constructor with type parameter.
	 * @param gson Gson istance (not null)
	 * @param value Value to serialize
	 * @param typeOfSrc Type of the object to serialize
	 */
	public GsonJsonWriter(Gson gson, Object value, Type typeOfSrc) {
		super();
		ObjectUtils.argumentNotNull(gson, "Gson instance must be not null");
		this.gson = gson;
		this.value = value;
		this.typeOfSrc = typeOfSrc;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonData#asString()
	 */
	@Override
	public String asString() {
		try {
			if (typeOfSrc != null) {
				return gson.toJson(value, typeOfSrc);
			} else {
				return gson.toJson(value);
			}
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonData#asBytes()
	 */
	@Override
	public byte[] asBytes() {
		return ConversionUtils.toBytes(asString());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonData#write(java.lang.Appendable)
	 */
	@Override
	public void write(Appendable writer) {
		ObjectUtils.argumentNotNull(writer, "Writer must be not null");
		try {
			if (typeOfSrc != null) {
				gson.toJson(value, typeOfSrc, writer);
			} else {
				gson.toJson(value, writer);
			}
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonData#write(java.io.OutputStream, java.nio.charset.Charset)
	 */
	@Override
	public void write(OutputStream stream, Charset charset) {
		try (OutputStreamWriter writer = new OutputStreamWriter(stream,
				(charset != null) ? charset : StandardCharsets.UTF_8)) {
			write(writer);
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

}
