package com.xebia.xoc.config;

public class PropertyMapperConfig<C extends AbstractClassMapperConfig<?>> extends AbstractElementMapperConfig<C, PropertyMapperConfig<C>> {
  
  private String target;
  
  public PropertyMapperConfig(C classMapperConfig) {
    super(classMapperConfig);
  }
  
  public String getTarget() {
    return target;
  }
  
  protected void setTarget(String target) {
    this.target = target;
  }
  
  @Override
  protected PropertyMapperConfig<C> getThis() {
    return this;
  }
  
}
