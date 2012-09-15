package com.xebia.xoc.impl.beanutils;

import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.impl.ClassMapperBuilder;

public class ConstructorArgumentExtractor extends AbstractElementExtractor {

  public ConstructorArgumentExtractor(String source, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
    super(source, converter, nestedClassMapperBuilder);
  }
  
}
