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
package com.holonplatform.json.internal.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.property.Property;

/**
 * Default {@link PropertyBoxSerializationNode} implementation.
 * 
 * @since 5.1.0
 */
public class DefaultPropertyBoxSerializationNode implements PropertyBoxSerializationNode {

	private final String name;
	private final Property<?> property;
	private List<PropertyBoxSerializationNode> children;

	/**
	 * Constructor for leaf nodes.
	 * @param name Serialization name
	 * @param property The property bound to this node
	 */
	public DefaultPropertyBoxSerializationNode(String name, Property<?> property) {
		super();
		this.name = name;
		this.property = property;
	}

	/**
	 * Constructor for non-leaf nodes.
	 * @param name Serialization name
	 */
	public DefaultPropertyBoxSerializationNode(String name) {
		super();
		this.name = name;
		this.property = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.jackson.internal.PropertyBoxSerializationNode#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.jackson.internal.PropertyBoxSerializationNode#getProperty()
	 */
	@Override
	public Optional<Property<?>> getProperty() {
		return Optional.ofNullable(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.json.jackson.internal.PropertyBoxSerializationNode#getChildren()
	 */
	@Override
	public List<PropertyBoxSerializationNode> getChildren() {
		return (children != null) ? children : Collections.emptyList();
	}

	/**
	 * Add a child node.
	 * @param node The node to add
	 */
	public void addChild(PropertyBoxSerializationNode node) {
		if (children == null) {
			children = new LinkedList<>();
		}
		children.add(node);
	}

}
