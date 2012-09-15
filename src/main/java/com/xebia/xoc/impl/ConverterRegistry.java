package com.xebia.xoc.impl;

import java.util.ArrayList;
import java.util.List;

import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.impl.NumberToStringConverter;
import com.xebia.xoc.conversion.impl.StringToIntegerConverter;

public class ConverterRegistry {
  
  private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
  
  public void registerDefaultConverters() {
    converters.add(new NumberToStringConverter());
    converters.add(new StringToIntegerConverter());
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Converter findConverter(Class sourceClass, Class targetClass) {
    for (Converter element : converters) {
      if (element.canConvert(sourceClass, targetClass)) {
        return element;
      }
    }
    return null;
  }
  
}