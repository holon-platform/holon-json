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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Adapter to use an {@link Appendable} as a {@link Writer}.
 * 
 * @since 5.1.0
 */
public class AppendableWriterAdapter extends Writer {

	/**
	 * Wrapped {@link Appendable}
	 */
	private final Appendable appendable;

	/**
	 * Constructor
	 * @param appendable Wrapped {@link Appendable} (not null)
	 */
	public AppendableWriterAdapter(Appendable appendable) {
		ObjectUtils.argumentNotNull(appendable, "Appendable must be not null");
		this.appendable = appendable;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		appendable.append(String.valueOf(cbuf), off, len);
	}

	@Override
	public void flush() throws IOException {
		if (appendable instanceof Flushable) {
			((Flushable) appendable).flush();
		}
	}

	@Override
	public void close() throws IOException {
		flush();
		if (appendable instanceof Closeable) {
			((Closeable) appendable).close();
		}
	}

}
