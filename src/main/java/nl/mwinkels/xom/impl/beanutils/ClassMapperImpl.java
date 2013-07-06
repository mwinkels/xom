package nl.mwinkels.xom.impl.beanutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.ConversionException;
import nl.mwinkels.xom.impl.AbstractClassMapper;
import nl.mwinkels.xom.impl.beanutils.processors.ValueProcessorFactory;

public class ClassMapperImpl<S, T> extends AbstractClassMapper<S, T> {

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
  protected T create(S source) throws MappingException {
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
        objects[i] = constructorArgument.apply(source, parameterTypes[i], valueProcessorFactory);
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
  protected T apply(S source, T target) throws MappingException {
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
