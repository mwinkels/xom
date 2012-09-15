package com.xebia.xoc.beanutils.processors;

import java.lang.reflect.Array;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.conversion.ConversionException;

public class ArrayToArrayProcessor implements ValueProcessor {

  private final ValueProcessor valueProcessor;
  private final Class<?> targetComponentType;

  public ArrayToArrayProcessor(Class<?> targetComponentType, ValueProcessor valueProcessor) {
    this.targetComponentType = targetComponentType;
    this.valueProcessor = valueProcessor;
  }

  @Override
  public Object process(Object value) throws MappingException, ConversionException {
    Object[] objects = (Object[])value;
    Object result = Array.newInstance(targetComponentType, objects.length);
    int i = 0;
    for (Object object : objects) {
      Array.set(result, i++, valueProcessor.process(object));
    }
    return result;
  }
  
}
