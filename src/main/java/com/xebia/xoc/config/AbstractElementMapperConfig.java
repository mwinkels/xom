package com.xebia.xoc.config;

import com.xebia.xoc.conversion.Converter;

@SuppressWarnings("rawtypes")
abstract class AbstractElementMapperConfig<T extends AbstractElementMapperConfig> {
  
  protected final ClassMapperConfig classMapperConfig;
  private String source;
  protected Converter converter;
  
  public AbstractElementMapperConfig(ClassMapperConfig classMapperConfig) {
    this.classMapperConfig = classMapperConfig;
  }
  
  public T from(String source) {
    this.source = source;
    return getThis();
  }
  
  public T withConverter(Converter converter) {
    this.converter = converter;
    return getThis();
  }
  
  protected abstract T getThis();
  
  public ClassMapperConfig add() {
    return classMapperConfig;
  }
  
  public String getSource() {
    return source;
  }
  
  public Converter getConverter() {
    return converter;
  }
  
}