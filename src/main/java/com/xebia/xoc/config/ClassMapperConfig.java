package com.xebia.xoc.config;

import java.util.ArrayList;
import java.util.List;

public class ClassMapperConfig {

	private final List<PropertyMapperConfig> properties = new ArrayList<PropertyMapperConfig>();
	private final ArrayList<ConstructorArgumentMapperConfig> constructorArguments = new ArrayList<ConstructorArgumentMapperConfig>();
	
	public ConstructorArgumentMapperConfig constructorArg(int i) {
		ConstructorArgumentMapperConfig constructorArgumentMapperConfig = new ConstructorArgumentMapperConfig(this, i);
		if (constructorArguments.size() > i) {
			constructorArguments.set(i, constructorArgumentMapperConfig);
		} else {
			constructorArguments.ensureCapacity(i);
			constructorArguments.add(i, constructorArgumentMapperConfig);
		}
		return constructorArgumentMapperConfig;
	}
	
	public PropertyMapperConfig property(String name) {
		PropertyMapperConfig propertyMapperConfig = new PropertyMapperConfig(this);
		propertyMapperConfig.setTarget(name);
		properties.add(propertyMapperConfig);
		return propertyMapperConfig;
	}
	
	public List<ConstructorArgumentMapperConfig> getConstructorArguments() {
		return constructorArguments;
	}
	
	public List<PropertyMapperConfig> getProperties() {
		return properties;
	}
}
