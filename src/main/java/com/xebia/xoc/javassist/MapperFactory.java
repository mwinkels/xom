package com.xebia.xoc.javassist;

import com.xebia.xoc.ClassMapperRegistry;
import com.xebia.xoc.config.AbstractClassMapperConfig;
import com.xebia.xoc.config.ConstructorArgumentMapperConfig;
import com.xebia.xoc.config.NestedClassMapperConfig;
import com.xebia.xoc.config.PropertyMapperConfig;
import com.xebia.xoc.conversion.ConverterRegistry;

public class MapperFactory {
  
  public ClassMapperBuilder fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
    ClassMapperBuilder classMapperBuilder = new ClassMapperBuilder();
    
    for (PropertyMapperConfig<?> propertyMapperConfig : config.getProperties()) {
      classMapperBuilder.addProperty(fromConfig(propertyMapperConfig, converterRegistry, mapperRegistry));
    }
    
    for (ConstructorArgumentMapperConfig<?> constructorArgumentMapperConfig : config.getConstructorArguments()) {
      classMapperBuilder.addConstructorArgument(fromConfig(constructorArgumentMapperConfig, converterRegistry, mapperRegistry));
    }
    
    return classMapperBuilder;
  }
  
  public PropertyMapperBuilder fromConfig(PropertyMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
    ClassMapperBuilder nestedBuilder = createNestedBuilder(config.getNestedClassMapperConfig(), converterRegistry, mapperRegistry);
    return new PropertyMapperBuilder(converterRegistry, mapperRegistry, config.getSource(), config.getTarget(), config.getConverter(), nestedBuilder);
  }
  
  public ConstructorArgumentMapperBuilder fromConfig(ConstructorArgumentMapperConfig<?> config, ConverterRegistry converterRegistry,
      ClassMapperRegistry mapperRegistry) {
    ClassMapperBuilder nestedBuilder = createNestedBuilder(config.getNestedClassMapperConfig(), converterRegistry, mapperRegistry);
    return new ConstructorArgumentMapperBuilder(converterRegistry, mapperRegistry, config.getSource(), config.getIndex(), config.getConverter(), nestedBuilder);
  }
  
  private ClassMapperBuilder createNestedBuilder(NestedClassMapperConfig<?> nestedClassMapperConfig, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
    return nestedClassMapperConfig == null ? null : fromConfig(nestedClassMapperConfig, converterRegistry, mapperRegistry);
  }
  
}
