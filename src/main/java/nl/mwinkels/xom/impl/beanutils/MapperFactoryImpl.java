package nl.mwinkels.xom.impl.beanutils;

import nl.mwinkels.xom.config.AbstractClassMapperConfig;
import nl.mwinkels.xom.config.ConstructorArgumentMapperConfig;
import nl.mwinkels.xom.config.NestedClassMapperConfig;
import nl.mwinkels.xom.config.PropertyMapperConfig;
import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ClassMapperBuilder;
import nl.mwinkels.xom.impl.ClassMapperRegistry;
import nl.mwinkels.xom.impl.ConverterRegistry;
import nl.mwinkels.xom.impl.MapperFactory;
import nl.mwinkels.xom.impl.beanutils.processors.ValueProcessorFactory;

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
