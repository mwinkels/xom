package com.xebia.xoc.impl.beanutils;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.impl.ClassMapperBuilder;
import com.xebia.xoc.impl.beanutils.processors.ValueProcessorFactory;

public class ConstructorArgumentExtractor extends AbstractElementExtractor {

  public ConstructorArgumentExtractor(String source, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
    super(source, converter, nestedClassMapperBuilder);
  }

  public Object apply(Object source, Class<?> targetType, ValueProcessorFactory valueProcessorFactory) throws MappingException, ConversionException {
    return extractValue(source, targetType, valueProcessorFactory);
  }
  
}
