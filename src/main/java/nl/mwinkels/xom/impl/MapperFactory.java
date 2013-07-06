package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.config.AbstractClassMapperConfig;

public interface MapperFactory {

    ClassMapperBuilder fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry);

}