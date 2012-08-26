package com.xebia.xoc.javassist;

import com.xebia.xoc.config.AbstractClassMapperConfig;
import com.xebia.xoc.config.ConstructorArgumentMapperConfig;
import com.xebia.xoc.config.NestedClassMapperConfig;
import com.xebia.xoc.config.PropertyMapperConfig;
import com.xebia.xoc.conversion.ConverterRegistry;

public class MapperFactory {
  
  public ClassMapperBuilder fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry) {
    ClassMapperBuilder classMapperBuilder = new ClassMapperBuilder();
    
    for (PropertyMapperConfig<?> propertyMapperConfig : config.getProperties()) {
      classMapperBuilder.addProperty(fromConfig(classMapperBuilder, converterRegistry, propertyMapperConfig));
    }
    
    for (ConstructorArgumentMapperConfig<?> constructorArgumentMapperConfig : config.getConstructorArguments()) {
      classMapperBuilder.addConstructorArgument(fromConfig(classMapperBuilder, converterRegistry, constructorArgumentMapperConfig));
    }
    
    return classMapperBuilder;
  }
  
  public PropertyMapperBuilder fromConfig(ClassMapperBuilder builder, ConverterRegistry converterRegistry, PropertyMapperConfig<?> config) {
    return new PropertyMapperBuilder(builder, converterRegistry, config.getSource(), config.getTarget(), config.getConverter(), createNestedBuilder(
        config.getNestedClassMapperConfig(), converterRegistry));
  }
  
  public ConstructorArgumentMapperBuilder fromConfig(ClassMapperBuilder classMapperBuilder, ConverterRegistry converterRegistry,
      ConstructorArgumentMapperConfig<?> config) {
    return new ConstructorArgumentMapperBuilder(classMapperBuilder, converterRegistry, config.getSource(), config.getIndex(), config.getConverter(),
        createNestedBuilder(config.getNestedClassMapperConfig(), converterRegistry));
  }
  
  private ClassMapperBuilder createNestedBuilder(NestedClassMapperConfig<?> nestedClassMapperConfig, ConverterRegistry converterRegistry) {
    return nestedClassMapperConfig == null ? null : fromConfig(nestedClassMapperConfig, converterRegistry);
  }
  
}
