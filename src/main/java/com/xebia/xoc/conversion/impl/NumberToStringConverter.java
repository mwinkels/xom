package com.xebia.xoc.conversion.impl;

import com.xebia.xoc.conversion.Converter;

public class NumberToStringConverter implements Converter<Number, String> {
  
  public String convert(Number value) {
    return value.toString();
  }
  
  @Override
  public boolean canConvert(Class<?> sourceClass, Class<?> targetCLass) {
    return Number.class.isAssignableFrom(sourceClass) && targetCLass == String.class;
  }
}
