package com.xebia.xoc.beanutils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.beanutils.processors.ValueProcessorFactory;
import com.xebia.xoc.builder.ClassMapperBuilder;
import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;

public class PropertyExtractor extends AbstractElementExtractor {

  private final String target;

  public PropertyExtractor(String source, String target, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
    super(source, converter, nestedClassMapperBuilder);
    this.target = target;
  }
  
  public void apply(Object source, Object target, ValueProcessorFactory valueProcessorFactory) throws MappingException, ConversionException {
    try {
      Class<?> targetType = PropertyUtils.getPropertyType(target, this.target);
      Object value = extractValue(source, targetType, valueProcessorFactory);
      PropertyUtils.setProperty(target, this.target, value);
    } catch (IllegalAccessException e) {
      throw new MappingException(e);
    } catch (InvocationTargetException e) {
      throw new MappingException(e);
    } catch (NoSuchMethodException e) {
      throw new MappingException(e);
    } catch (MappingException e) {
      throw new MappingException(e);
    }
  }

}
