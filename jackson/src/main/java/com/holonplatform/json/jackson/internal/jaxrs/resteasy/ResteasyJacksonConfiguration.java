/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.json.jackson.internal.jaxrs.resteasy;

import java.io.Serializable;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.json.jackson.internal.JacksonLogger;

/**
 * TODO
 */
public final class ResteasyJacksonConfiguration implements Serializable {

	private static final long serialVersionUID = -483627221975315652L;
	
	private final static Logger LOGGER = JacksonLogger.create();
	
	/**
	 * Whether Resteasy is available from classpath of current ClassLoader
	 */
	public static final boolean RESTEASY_PRESENT = ClassUtils.isPresent("org.jboss.resteasy.spi.ResteasyDeployment",
			ClassUtils.getDefaultClassLoader());
	
	static {
		initProviders();
	}
	
	private static void initProviders() {
		//ResteasyProviderFactory.getInstance().
	}
	
}
