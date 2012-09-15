package com.xebia.xoc;

import com.xebia.xoc.builder.ClassMapperBuilder;
import com.xebia.xoc.config.AbstractClassMapperConfig;
import com.xebia.xoc.conversion.ConverterRegistry;

public interface MapperFactory {
  
  ClassMapperBuilder fromConfig(AbstractClassMapperConfig<?> config, ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry);
  
}