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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

import javax.annotation.Priority;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.json.JsonProvider;

/**
 * Default {@link JsonProvider}s handler by {@link ClassLoader}.
 *
 * @since 5.1.0
 */
public final class JsonProviders {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = JsonLogger.create();

	/**
	 * {@link Priority} based comparator.
	 */
	private static final Comparator<Object> PRIORITY_COMPARATOR = Comparator.comparingInt(
			p -> p.getClass().isAnnotationPresent(Priority.class) ? p.getClass().getAnnotation(Priority.class).value()
					: JsonProvider.DEFAULT_PRIORITY);

	/**
	 * Providers by ClassLoader
	 */
	private static final Map<ClassLoader, List<JsonProvider>> PROVIDERS = new WeakHashMap<>();

	private JsonProviders() {
	}

	/**
	 * Get the default {@link JsonProvider}, if any. The default {@link JsonProvider} is the first registered provider,
	 * i.e. the one with higher priority.
	 * @return Optional default {@link JsonProvider}
	 */
	public static Optional<JsonProvider> getDefaultJsonProvider() {
		return getDefaultJsonProvider(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Get the default {@link JsonProvider}, if any, for given ClassLoader. The default {@link JsonProvider} is the
	 * first registered provider, i.e. the one with higher priority.
	 * @param classLoader ClassLoader to use
	 * @return Optional default {@link JsonProvider}
	 */
	public static Optional<JsonProvider> getDefaultJsonProvider(ClassLoader classLoader) {
		ClassLoader cl = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();
		List<JsonProvider> providers = getJsonProviders(cl);
		if (!providers.isEmpty()) {
			return Optional.of(providers.get(0));
		}
		return Optional.empty();
	}

	/**
	 * Get the registered {@link JsonProviders}s for given ClassLoader.
	 * @param classLoader ClassLoader to use (not null)
	 * @return Providers list
	 */
	public static List<JsonProvider> getJsonProviders(ClassLoader classLoader) {
		ObjectUtils.argumentNotNull(classLoader, "ClassLoader must be not null");
		ensureInited(classLoader);
		return PROVIDERS.get(classLoader);
	}

	/**
	 * Ensure the JsonProviders are inited for given classloader.
	 * @param classLoader ClassLoader to use
	 */
	private static synchronized void ensureInited(final ClassLoader classLoader) {
		if (!PROVIDERS.containsKey(classLoader)) {

			LOGGER.debug(() -> "Load JsonProviders for classloader [" + classLoader
					+ "] using ServiceLoader with service name: " + JsonProvider.class.getName());

			final List<JsonProvider> results = new LinkedList<>();
			// load from META-INF/services
			Iterable<JsonProvider> loaded = AccessController
					.doPrivileged(new PrivilegedAction<Iterable<JsonProvider>>() {
						@Override
						public Iterable<JsonProvider> run() {
							return ServiceLoader.load(JsonProvider.class, classLoader);
						}
					});
			loaded.forEach(l -> {
				results.add(l);
				LOGGER.debug(() -> "Registered JsonProvider [" + l + "] for classloader [" + classLoader + "]");
			});
			Collections.sort(results, PRIORITY_COMPARATOR);

			PROVIDERS.put(classLoader, results);
		}
	}

}
