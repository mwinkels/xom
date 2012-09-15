package com.xebia.xoc.beanutils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.ClassMapper;
import com.xebia.xoc.MappingException;
import com.xebia.xoc.beanutils.processors.ValueProcessor;
import com.xebia.xoc.beanutils.processors.ValueProcessorFactory;
import com.xebia.xoc.builder.ClassMapperBuilder;
import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;

public abstract class AbstractElementExtractor {
  
  protected final String source;

  private final ClassMapperBuilder nestedClassMapperBuilder;
  
  private Converter<Object, Object> converter;
  
  public AbstractElementExtractor(String source, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
    this.source = source;
    this.converter = converter;
    this.nestedClassMapperBuilder = nestedClassMapperBuilder;
  }

  protected Object extractValue(Object source, Class<?> targetType, ValueProcessorFactory valueProcessorFactory) throws MappingException, ConversionException {
    Object value = getPropertyValue(source);
    ValueProcessor valueProcessor = valueProcessorFactory.create(value.getClass(), targetType, createNestedMapper(targetType, value), converter);
    return valueProcessor.process(value);
  }

  private ClassMapper<Object, Object> createNestedMapper(Class<?> targetType, Object value) {
    return nestedClassMapperBuilder == null ? null : (ClassMapper<Object, Object>) nestedClassMapperBuilder.build(value.getClass(), targetType);
  }

  private Object getPropertyValue(Object source) throws MappingException {
    if (StringUtils.isBlank(this.source)) {
      return source;
    }
    try {
      return PropertyUtils.getProperty(source, this.source);
    } catch (IllegalAccessException e) {
      throw new MappingException(e);
    } catch (InvocationTargetException e) {
      throw new MappingException(e);
    } catch (NoSuchMethodException e) {
      throw new MappingException(e);
    }
  }
  
}
