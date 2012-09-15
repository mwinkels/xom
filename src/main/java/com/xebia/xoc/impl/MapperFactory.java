package com.xebia.xoc.impl;

import com.xebia.xoc.config.AbstractClassMapperConfig;

public interface MapperFactory {
  
  ClassMapperBuilder fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry);
  
}