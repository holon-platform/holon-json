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
package com.holonplatform.json.jackson.internal;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.json.Json.JsonWriteException;
import com.holonplatform.json.JsonWriter;
import com.holonplatform.json.internal.AppendableWriterAdapter;

/**
 * Jackson implementation of the {@link JsonWriter}.
 *
 * @since 5.1.0
 */
public class JacksonJsonWriter implements JsonWriter {

	private final ObjectWriter writer;
	private final Object value;

	/**
	 * Constructor
	 * @param mapper Jackson {@link ObjectMapper} (not null)
	 * @param value Value to serialize
	 */
	public JacksonJsonWriter(ObjectMapper mapper, Object value) {
		super();
		ObjectUtils.argumentNotNull(mapper, "ObjectMapper must be not null");
		this.writer = mapper.writer();
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonWriter#asString()
	 */
	@Override
	public String asString() {
		try {
			return writer.writeValueAsString(value);
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonWriter#asBytes()
	 */
	@Override
	public byte[] asBytes() {
		try {
			return writer.writeValueAsBytes(value);
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonWriter#write(java.lang.Appendable)
	 */
	@SuppressWarnings("resource")
	@Override
	public void write(Appendable writer) {
		try {
			this.writer.writeValue((writer instanceof Writer) ? (Writer) writer : new AppendableWriterAdapter(writer),
					value);
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonWriter#write(java.io.OutputStream, java.nio.charset.Charset)
	 */
	@Override
	public void write(OutputStream stream, Charset charset) {
		try {
			this.writer.writeValue(stream, value);
		} catch (Exception e) {
			throw new JsonWriteException("Failed to write value [" + value + "] as JSON", e);
		}
	}

}
