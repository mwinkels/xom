package com.xebia.xoc.beanutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.xebia.xoc.ClassMapper;
import com.xebia.xoc.MappingException;
import com.xebia.xoc.beanutils.processors.ValueProcessorFactory;
import com.xebia.xoc.conversion.ConversionException;

public class ClassMapperImpl<S, T> implements ClassMapper<S, T> {

  private final List<ConstructorArgumentExtractor> constructorArguments;
  private final List<PropertyExtractor> properties;
  private final Class<S> sourceClass;
  private final Class<T> targetClass;
  private final ValueProcessorFactory valueProcessorFactory;

  public ClassMapperImpl(ValueProcessorFactory valueProcessorFactory, List<ConstructorArgumentExtractor> constructorArguments, List<PropertyExtractor> properties, Class<S> sourceClass, Class<T> targetClass) {
    this.valueProcessorFactory = valueProcessorFactory;
    this.constructorArguments = constructorArguments;
    this.properties = properties;
    this.sourceClass = sourceClass;
    this.targetClass = targetClass;
  }

  @Override
  public T map(S source) throws MappingException {
    return map(source, create(source));
  }

  private T create(S source) throws MappingException {
    try {
      Constructor<T> constructor = findConstructor();
      return (T) constructor.newInstance(convertConstructorArguments(source, constructor.getParameterTypes()));
    } catch (IllegalAccessException e) {
      throw new MappingException(e);
    } catch (InvocationTargetException e) {
      throw new MappingException(e);
    } catch (InstantiationException e) {
      throw new MappingException(e);
    }
  }
  
  private Constructor<T> findConstructor() throws MappingException {
    for (Constructor<?> c : targetClass.getConstructors()) {
      if (c.getParameterTypes().length == constructorArguments.size()) {
        return (Constructor<T>) c;
      }
    }
    throw new MappingException("No suitable constructor for {} parameters in class {}.", constructorArguments.size(), targetClass);
  }

  private Object[] convertConstructorArguments(S source, Class<?>[] parameterTypes) throws MappingException {
    Object[] objects = new Object[constructorArguments.size()];
    int i = 0;
    MappingConversionException mappingConversionException = null;
    for (ConstructorArgumentExtractor constructorArgument : constructorArguments) {
      try {
        objects[i] = constructorArgument.extractValue(source, parameterTypes[i], valueProcessorFactory);
        i++;
      } catch (ConversionException e) {
        if (mappingConversionException == null) {
          mappingConversionException = new MappingConversionException();
        }
        mappingConversionException.add(e);
      }
    }
    if (mappingConversionException != null) {
      throw mappingConversionException;
    }
    return objects;
  }
  
  @Override
  public T map(S source, T target) throws MappingException {
    MappingConversionException mappingConversionException = null;
    for (PropertyExtractor propertyExtractor : properties) {
      try {
        propertyExtractor.apply(source, target, valueProcessorFactory);
      } catch (ConversionException e) {
        if (mappingConversionException == null) {
          mappingConversionException = new MappingConversionException();
        }
        mappingConversionException.add(e);
      }
    }
    if (mappingConversionException != null) {
      throw mappingConversionException;
    }
    return target;
  }
  
}
