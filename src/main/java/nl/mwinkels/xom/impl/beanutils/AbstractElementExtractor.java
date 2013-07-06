package nl.mwinkels.xom.impl.beanutils;

import java.lang.reflect.InvocationTargetException;

import nl.mwinkels.xom.conversion.ConversionException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ClassMapper;
import nl.mwinkels.xom.impl.ClassMapperBuilder;
import nl.mwinkels.xom.impl.beanutils.processors.ValueProcessor;
import nl.mwinkels.xom.impl.beanutils.processors.ValueProcessorFactory;

abstract class AbstractElementExtractor {
  
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
