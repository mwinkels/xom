package com.xebia.xoc.config;

/**
 * Configuration for a nested class mapper.
 * <p>
 * A nested class mapper is a mapper used to map a number of elements (properties and constructor arguments) on a nested structure (class) in a class.
 * 
 * @author mwinkels@xebia.com
 * @param <C> The type of the container of this nested class mapper.
 */
public class NestedClassMapperConfig<C extends AbstractClassMapperConfig<?>> extends AbstractClassMapperConfig<NestedClassMapperConfig<C>> {
  
  private final C classMapperConfig;
  
  public NestedClassMapperConfig(C classMapperConfig) {
    this.classMapperConfig = classMapperConfig;
  }
  
  /**
   * Fluent method to indicate that the configuration for this nested class is done.
   * @return the container.
   */
  public C add() {
    return classMapperConfig;
  }
  
  @Override
  protected NestedClassMapperConfig<C> getThis() {
    return this;
  }
  
}
