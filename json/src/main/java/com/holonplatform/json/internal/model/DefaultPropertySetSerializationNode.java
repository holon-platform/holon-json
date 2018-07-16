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
package com.holonplatform.json.internal.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.json.model.PropertySetSerializationNode;

/**
 * Default {@link PropertySetSerializationNode} implementation.
 * 
 * @since 5.1.0
 */
public class DefaultPropertySetSerializationNode implements PropertySetSerializationNode {

	private final String name;
	private final Property<?> property;
	private List<PropertySetSerializationNode> children;

	/**
	 * Constructor for leaf nodes.
	 * @param name Serialization name
	 * @param property The property bound to this node
	 */
	public DefaultPropertySetSerializationNode(String name, Property<?> property) {
		super();
		ObjectUtils.argumentNotNull(name, "Serialization name must be not null");
		ObjectUtils.argumentNotNull(property, "Serialization property must be not null");
		this.name = name;
		this.property = property;
	}

	/**
	 * Constructor for non-leaf nodes.
	 * @param name Serialization name
	 */
	public DefaultPropertySetSerializationNode(String name) {
		super();
		ObjectUtils.argumentNotNull(name, "Serialization name must be not null");
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
	public List<PropertySetSerializationNode> getChildren() {
		return (children != null) ? Collections.unmodifiableList(children) : Collections.emptyList();
	}

	/**
	 * Add a child node.
	 * @param node The node to add
	 */
	public void addChild(PropertySetSerializationNode node) {
		ObjectUtils.argumentNotNull(node, "Node to add must be not null");
		if (children == null) {
			children = new LinkedList<>();
		}
		children.add(node);
	}

}
