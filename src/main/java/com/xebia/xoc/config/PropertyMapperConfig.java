package com.xebia.xoc.config;

public class PropertyMapperConfig extends AbstractElementMapperConfig<PropertyMapperConfig> {

	private String target;
	public PropertyMapperConfig(ClassMapperConfig classMapperConfig) {
		super(classMapperConfig);
	}

	public String getTarget() {
		return target;
	}
	
	protected void setTarget(String target) {
		this.target = target;
	}
	
	@Override
	protected PropertyMapperConfig getThis() {
		return this;
	}

}
