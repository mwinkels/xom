package com.xebia.xoc.config;

import com.xebia.xoc.conversion.Converter;

/**
 * Common super class of all elements that can be container in a {@link AbstractClassMapperConfig}.
 * 
 * @author mwinkels@xebia.com
 *
 * @param <C> The container type of this element.
 * @param <T> The actual (sub) type of this element.
 */
abstract class AbstractElementMapperConfig<C extends AbstractClassMapperConfig<?>, T extends AbstractElementMapperConfig<C, ?>> {
  
  protected final C classMapperConfig;
  private String source;
  protected Converter<?, ?> converter;
  private NestedClassMapperConfig<C> nestedClassMapperConfig;
  
  public AbstractElementMapperConfig(C classMapperConfig) {
    this.classMapperConfig = classMapperConfig;
  }
  
  /**
   * Fluent method used to set the source property to map.
   * 
   * @param source The name of the soure property.
   * @return this.
   */
  public T from(String source) {
    this.source = source;
    return getThis();
  }
  
  /**
   * Fluent method used to set the converter to use for this element.
   * @param converter The converter to use.
   * @return this
   */
  public T withConverter(Converter<?, ?> converter) {
    this.converter = converter;
    return getThis();
  }
  
  /**
   * Fluent method used to indicate that this element is configured.
   * @return the container of this element.
   */
  public C add() {
    return classMapperConfig;
  }

  /**
   * Fluent method used to indicate that the element should be mapped with a new nested mapper.
   * @return the new nested mapper.
   */
  public NestedClassMapperConfig<C> withMapper() {
    nestedClassMapperConfig = new NestedClassMapperConfig<C>(classMapperConfig);
    return nestedClassMapperConfig;
  }
  
  public String getSource() {
    return source;
  }
  
  public Converter<?, ?> getConverter() {
    return converter;
  }
  
  public NestedClassMapperConfig<C> getNestedClassMapperConfig() {
    return nestedClassMapperConfig;
  }
  
  /**
   * Used to ensure that the fluent methods return the correct type. 
   * 
   * @return this
   */
  protected abstract T getThis();
  
}