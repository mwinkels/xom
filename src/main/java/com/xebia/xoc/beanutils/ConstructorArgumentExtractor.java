package com.xebia.xoc.beanutils;

import com.xebia.xoc.builder.ClassMapperBuilder;
import com.xebia.xoc.conversion.Converter;

public class ConstructorArgumentExtractor extends AbstractElementExtractor {

  public ConstructorArgumentExtractor(String source, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
    super(source, converter, nestedClassMapperBuilder);
  }
  
}
