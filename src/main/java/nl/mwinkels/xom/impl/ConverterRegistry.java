package nl.mwinkels.xom.impl;

import java.util.ArrayList;
import java.util.List;

import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.conversion.impl.NumberToStringConverter;
import nl.mwinkels.xom.conversion.impl.StringToIntegerConverter;

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
