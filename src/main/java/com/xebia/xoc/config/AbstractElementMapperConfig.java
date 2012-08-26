package com.xebia.xoc.config;

import com.xebia.xoc.conversion.Converter;

abstract class AbstractElementMapperConfig<C extends AbstractClassMapperConfig<?>, T extends AbstractElementMapperConfig<C, ?>> {
  
  protected final C classMapperConfig;
  private String source;
  protected Converter<?, ?> converter;
  
  public AbstractElementMapperConfig(C classMapperConfig) {
    this.classMapperConfig = classMapperConfig;
  }
  
  public T from(String source) {
    this.source = source;
    return getThis();
  }
  
  public T withConverter(Converter<?, ?> converter) {
    this.converter = converter;
    return getThis();
  }
  
  public C add() {
    return classMapperConfig;
  }
  
  public String getSource() {
    return source;
  }
  
  public Converter<?, ?> getConverter() {
    return converter;
  }

  public NestedClassMapperConfig<C> withMapper() {
    return new NestedClassMapperConfig<C>(classMapperConfig);
  }
  
  protected abstract T getThis();
  
}