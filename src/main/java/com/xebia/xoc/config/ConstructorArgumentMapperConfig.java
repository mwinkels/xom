package com.xebia.xoc.config;

public class ConstructorArgumentMapperConfig<C extends AbstractClassMapperConfig<?>> extends AbstractElementMapperConfig<C, ConstructorArgumentMapperConfig<C>> {
  
  private final int index;
  
  public ConstructorArgumentMapperConfig(C classMapperConfig, int index) {
    super(classMapperConfig);
    this.index = index;
  }
  
  @Override
  protected ConstructorArgumentMapperConfig<C> getThis() {
    return this;
  }
  
  public int getIndex() {
    return index;
  }
  
}
