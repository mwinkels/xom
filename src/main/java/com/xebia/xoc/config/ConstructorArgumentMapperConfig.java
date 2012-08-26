package com.xebia.xoc.config;

public class ConstructorArgumentMapperConfig extends AbstractElementMapperConfig<ConstructorArgumentMapperConfig> {

	private final int index;

	public ConstructorArgumentMapperConfig(ClassMapperConfig classMapperConfig, int index) {
		super(classMapperConfig);
		this.index = index;
	}

	@Override
	protected ConstructorArgumentMapperConfig getThis() {
		return this;
	}

	public int getIndex() {
		return index;
	}

}
