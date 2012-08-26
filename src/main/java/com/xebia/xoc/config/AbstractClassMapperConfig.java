package com.xebia.xoc.config;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractClassMapperConfig<C extends AbstractClassMapperConfig<?>> {
  
  private final List<PropertyMapperConfig<C>> properties = new ArrayList<PropertyMapperConfig<C>>();
  private final ArrayList<ConstructorArgumentMapperConfig<C>> constructorArguments = new ArrayList<ConstructorArgumentMapperConfig<C>>();
  
  public ConstructorArgumentMapperConfig<C> constructorArg(int i) {
    ConstructorArgumentMapperConfig<C> constructorArgumentMapperConfig = new ConstructorArgumentMapperConfig<C>(getThis(), i);
    if (constructorArguments.size() > i) {
      constructorArguments.set(i, constructorArgumentMapperConfig);
    } else {
      constructorArguments.ensureCapacity(i);
      constructorArguments.add(i, constructorArgumentMapperConfig);
    }
    return constructorArgumentMapperConfig;
  }
  
  public PropertyMapperConfig<C> property(String name) {
    PropertyMapperConfig<C> propertyMapperConfig = new PropertyMapperConfig<C>(getThis());
    propertyMapperConfig.setTarget(name);
    properties.add(propertyMapperConfig);
    return propertyMapperConfig;
  }
  
  public List<ConstructorArgumentMapperConfig<C>> getConstructorArguments() {
    return constructorArguments;
  }
  
  public List<PropertyMapperConfig<C>> getProperties() {
    return properties;
  }
  
  protected abstract C getThis();
}