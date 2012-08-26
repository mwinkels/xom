package com.xebia.xoc.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Common super class of {@link ClassMapperConfig}s and {@link NestedClassMapperConfig}. Can hold {@link PropertyMapperConfig}s and
 * {@link ConstructorArgumentMapperConfig}s.
 * <p>
 * The {@link PropertyMapperConfig}s and {@link ConstructorArgumentMapperConfig}s returned and contained by instances will have the correct container
 * type set, so they can be used correctly in fluent style.
 * 
 * @author mwinkels@xebia.com
 * @param <C> The actual (sub)type.
 * @see ClassMapperConfig
 * @see NestedClassMapperConfig
 */
public abstract class AbstractClassMapperConfig<C extends AbstractClassMapperConfig<?>> {
  
  private final List<PropertyMapperConfig<C>> properties = new ArrayList<PropertyMapperConfig<C>>();
  private final ArrayList<ConstructorArgumentMapperConfig<C>> constructorArguments = new ArrayList<ConstructorArgumentMapperConfig<C>>();
  
  /**
   * Constructs and returns a new {@link ConstructorArgumentMapperConfig} contained in this config.
   * 
   * @param index the index of the constructor argument.
   * @return A new {@link ConstructorArgumentMapperConfig} contained in this config.
   */
  public ConstructorArgumentMapperConfig<C> constructorArg(int index) {
    ConstructorArgumentMapperConfig<C> constructorArgumentMapperConfig = new ConstructorArgumentMapperConfig<C>(getThis(), index);
    if (constructorArguments.size() > index) {
      constructorArguments.set(index, constructorArgumentMapperConfig);
    } else {
      constructorArguments.ensureCapacity(index);
      constructorArguments.add(index, constructorArgumentMapperConfig);
    }
    return constructorArgumentMapperConfig;
  }
  
  /**
   * Constructs and returns a new {@link PropertyMapperConfig} contained in this config.
   * 
   * @param name the name of the property.
   * @return A new {@link PropertyMapperConfig} contained in this config.
   */
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
  
  /**
   * Used to ensure that the contained elements are constructed with the correct container type (C).
   * 
   * @return this.
   */
  protected abstract C getThis();
}