package com.xebia.xoc.config;

/**
 * Configuration for mapping a constructor argument.
 * 
 * @author mwinkels@xebia.com
 * @param <C> the type of the container of this constructor argument.
 */
public class ConstructorArgumentMapperConfig<C extends AbstractClassMapperConfig<?>> extends
    AbstractElementMapperConfig<C, ConstructorArgumentMapperConfig<C>> {
  
  private final int index;
  
  public ConstructorArgumentMapperConfig(C classMapperConfig, int index) {
    super(classMapperConfig);
    this.index = index;
  }
  
  public int getIndex() {
    return index;
  }
  
  @Override
  protected ConstructorArgumentMapperConfig<C> getThis() {
    return this;
  }
  
}
