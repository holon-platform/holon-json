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
package com.holonplatform.json.internal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.json.JsonReader;

/**
 * Default {@link JsonReader} implementation.
 * 
 * @since 5.1.0
 */
public class DefaultJsonReader implements JsonReader {

	/**
	 * Actual reader
	 */
	private final Reader reader;

	/**
	 * Constructor using an {@link InputStream} and default UTF-8 encoding.
	 * @param stream The JSON {@link InputStream}
	 */
	public DefaultJsonReader(InputStream stream) {
		this(stream, StandardCharsets.UTF_8);
	}

	/**
	 * Constructor using an {@link InputStream}.
	 * @param stream The JSON {@link InputStream}
	 * @param charsetName Encoding charset name
	 */
	public DefaultJsonReader(InputStream stream, String charsetName) {
		this(stream, (charsetName != null) ? Charset.forName(charsetName) : StandardCharsets.UTF_8);
	}

	/**
	 * Constructor using an {@link InputStream}.
	 * @param stream The JSON {@link InputStream}
	 * @param charset Encoding charset (if <code>null</code>, default UTF-8 will be used)
	 */
	public DefaultJsonReader(InputStream stream, Charset charset) {
		this(new InputStreamReader(stream, (charset != null) ? charset : StandardCharsets.UTF_8));
	}

	/**
	 * Constructor using a byte array.
	 * @param bytes JSON bytes
	 */
	public DefaultJsonReader(byte[] bytes) {
		this((bytes == null) ? "" : new String(bytes, StandardCharsets.UTF_8));
	}

	/**
	 * Constructor using a String.
	 * @param string JSON string
	 */
	public DefaultJsonReader(String string) {
		this(new StringReader((string != null) ? string : ""));
	}

	/**
	 * Constructor using a generic {@link Reader}.
	 * @param reader JSON Reader (not null)
	 */
	public DefaultJsonReader(Reader reader) {
		super();
		ObjectUtils.argumentNotNull(reader, "Reader must be not null");
		this.reader = reader;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.JsonReader#getReader()
	 */
	@Override
	public Reader getReader() {
		return reader;
	}

}
