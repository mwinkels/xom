package com.xebia.xoc.config;

public class NestedClassMapperConfig<C extends AbstractClassMapperConfig<?>> extends AbstractClassMapperConfig<NestedClassMapperConfig<C>> {

  private final C classMapperConfig;

  public NestedClassMapperConfig(C classMapperConfig) {
    this.classMapperConfig = classMapperConfig;
  }

  public C add() {
    return classMapperConfig;
  }

  @Override
  protected NestedClassMapperConfig<C> getThis() {
    return this;
  }
  
}
