package nl.mwinkels.xom.impl.javassist;

import nl.mwinkels.xom.config.AbstractClassMapperConfig;
import nl.mwinkels.xom.config.ConstructorArgumentMapperConfig;
import nl.mwinkels.xom.config.NestedClassMapperConfig;
import nl.mwinkels.xom.config.PropertyMapperConfig;
import nl.mwinkels.xom.impl.ClassMapperRegistry;
import nl.mwinkels.xom.impl.ConverterRegistry;
import nl.mwinkels.xom.impl.MapperFactory;

public class MapperFactoryImpl implements MapperFactory {

    /* (non-Javadoc)
     * @see com.mwinkels.xom.javassist.MapperFactory#fromConfig(AbstractClassMapperConfig, com.mwinkels.xom.conversion.ConverterRegistry, com.mwinkels.xom.ClassMapperRegistry)
     */
    @Override
    public ClassMapperBuilderImpl fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
        ClassMapperBuilderImpl classMapperBuilder = new ClassMapperBuilderImpl();

        for (PropertyMapperConfig<?> propertyMapperConfig : config.getProperties()) {
            classMapperBuilder.addProperty(fromConfig(propertyMapperConfig, converterRegistry, mapperRegistry));
        }

        for (ConstructorArgumentMapperConfig<?> constructorArgumentMapperConfig : config.getConstructorArguments()) {
            classMapperBuilder.addConstructorArgument(fromConfig(constructorArgumentMapperConfig, converterRegistry, mapperRegistry));
        }

        return classMapperBuilder;
    }

    public PropertyMapperBuilder fromConfig(PropertyMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
        ClassMapperBuilderImpl nestedBuilder = createNestedBuilder(config.getNestedClassMapperConfig(), converterRegistry, mapperRegistry);
        return new PropertyMapperBuilder(converterRegistry, mapperRegistry, config.getSource(), config.getTarget(), config.getConverter(), nestedBuilder);
    }

    public ConstructorArgumentMapperBuilder fromConfig(ConstructorArgumentMapperConfig<?> config, ConverterRegistry converterRegistry,
                                                       ClassMapperRegistry mapperRegistry) {
        ClassMapperBuilderImpl nestedBuilder = createNestedBuilder(config.getNestedClassMapperConfig(), converterRegistry, mapperRegistry);
        return new ConstructorArgumentMapperBuilder(converterRegistry, mapperRegistry, config.getSource(), config.getIndex(), config.getConverter(), nestedBuilder);
    }

    private ClassMapperBuilderImpl createNestedBuilder(NestedClassMapperConfig<?> nestedClassMapperConfig, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
        return nestedClassMapperConfig == null ? null : fromConfig(nestedClassMapperConfig, converterRegistry, mapperRegistry);
    }

}
