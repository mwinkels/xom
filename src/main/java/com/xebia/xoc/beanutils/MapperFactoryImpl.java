package com.xebia.xoc.beanutils;

import com.xebia.xoc.ClassMapperRegistry;
import com.xebia.xoc.MapperFactory;
import com.xebia.xoc.beanutils.processors.ValueProcessorFactory;
import com.xebia.xoc.builder.ClassMapperBuilder;
import com.xebia.xoc.config.AbstractClassMapperConfig;
import com.xebia.xoc.config.ConstructorArgumentMapperConfig;
import com.xebia.xoc.config.NestedClassMapperConfig;
import com.xebia.xoc.config.PropertyMapperConfig;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

public class MapperFactoryImpl implements MapperFactory {
  
  @Override
  public ClassMapperBuilder fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
    ClassMapperBuilderImpl classMapperBuilder = new ClassMapperBuilderImpl(new ValueProcessorFactory(converterRegistry, mapperRegistry));
    for (ConstructorArgumentMapperConfig<?> constructorArgumentMapperConfig : config.getConstructorArguments()) {
      ClassMapperBuilder nestedClassMapperBuilder = createNestedBuidler(constructorArgumentMapperConfig.getNestedClassMapperConfig(), converterRegistry, mapperRegistry);
      classMapperBuilder.addConstructorArgumentExtractor(fromConfig(constructorArgumentMapperConfig, nestedClassMapperBuilder));
    }
    for (PropertyMapperConfig<?> propertyMapperConfig : config.getProperties()) {
      ClassMapperBuilder nestedClassMapperBuilder = createNestedBuidler(propertyMapperConfig.getNestedClassMapperConfig(), converterRegistry, mapperRegistry);
      classMapperBuilder.addPropertyExtactor(fromConfig(propertyMapperConfig, nestedClassMapperBuilder));
    }
    return classMapperBuilder;
  }
  
  private ConstructorArgumentExtractor fromConfig(ConstructorArgumentMapperConfig<?> config, ClassMapperBuilder nestedClassMapperBuilder) {
    return new ConstructorArgumentExtractor(config.getSource(), (Converter<Object, Object>) config.getConverter(), nestedClassMapperBuilder);
  }

  private PropertyExtractor fromConfig(PropertyMapperConfig<?> config, ClassMapperBuilder nestedClassMapperBuilder) {
    return new PropertyExtractor(config.getSource(), config.getTarget(), (Converter<Object, Object>) config.getConverter(), nestedClassMapperBuilder);
  }
  
  private ClassMapperBuilder createNestedBuidler(NestedClassMapperConfig<?> nestedClassMapperConfig, ConverterRegistry converterRegistry,
      ClassMapperRegistry mapperRegistry) {
    return nestedClassMapperConfig == null ? null : fromConfig(nestedClassMapperConfig, converterRegistry, mapperRegistry);
  }
  
}
