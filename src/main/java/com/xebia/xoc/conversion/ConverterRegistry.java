package com.xebia.xoc.conversion;

import java.util.ArrayList;
import java.util.List;

import com.xebia.xoc.conversion.impl.NumberToStringConverter;
import com.xebia.xoc.conversion.impl.StringToIntegerConverter;

public class ConverterRegistry {
  
  private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
  
  public void registerDefaultConverters() {
    converters.add(new NumberToStringConverter());
    converters.add(new StringToIntegerConverter());
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Converter findConverter(Class sourceClass, Class targetCLass) {
    for (Converter element : converters) {
      if (element.canConvert(sourceClass, targetCLass)) {
        return element;
      }
    }
    return null;
  }
  
}
