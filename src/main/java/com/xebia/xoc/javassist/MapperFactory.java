package com.xebia.xoc.javassist;

import com.xebia.xoc.config.ClassMapperConfig;
import com.xebia.xoc.config.ConstructorArgumentMapperConfig;
import com.xebia.xoc.config.PropertyMapperConfig;
import com.xebia.xoc.conversion.ConverterRegistry;

public class MapperFactory {
  
  public <S, T> ClassMapperBuilder<S, T> fromConfig(ClassMapperConfig config, ConverterRegistry converterRegistry, Class<S> sourceClass,
      Class<T> targetClass) {
    ClassMapperBuilder<S, T> classMapperBuilder = new ClassMapperBuilder<S, T>(sourceClass, targetClass);
    
    for (PropertyMapperConfig propertyMapperConfig : config.getProperties()) {
      classMapperBuilder.addProperty(fromConfig(classMapperBuilder, converterRegistry, propertyMapperConfig));
    }
    
    for (ConstructorArgumentMapperConfig constructorArgumentMapperConfig : config.getConstructorArguments()) {
      classMapperBuilder.addConstructorArgument(fromConfig(classMapperBuilder, converterRegistry, constructorArgumentMapperConfig));
    }
    
    return classMapperBuilder;
  }
  
  public <S, T> PropertyMapperBuilder fromConfig(ClassMapperBuilder<S, T> classMapperBuilder, ConverterRegistry converterRegistry,
      PropertyMapperConfig config) {
    return new PropertyMapperBuilder(classMapperBuilder, converterRegistry, config.getSource(), config.getTarget(), config.getConverter());
  }
  
  public <S, T> ConstructorArgumentMapperBuilder fromConfig(ClassMapperBuilder<S, T> classMapperBuilder, ConverterRegistry converterRegistry,
      ConstructorArgumentMapperConfig config) {
    return new ConstructorArgumentMapperBuilder(classMapperBuilder, converterRegistry, config.getSource(), config.getIndex(), config.getConverter());
  }
}
