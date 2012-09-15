package com.xebia.xoc.impl.beanutils.processors;

import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;

public class ConverterProcessor implements ValueProcessor {
  
  private final Converter<Object, Object> converter;

  public ConverterProcessor(Converter<Object, Object> converter) {
    this.converter = converter;
  }

  @Override
  public Object process(Object value) throws ConversionException {
    return converter.convert(value);
  }
  
}
