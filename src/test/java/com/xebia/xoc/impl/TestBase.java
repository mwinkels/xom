package com.xebia.xoc.impl;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.xebia.xoc.impl.MapperFactory;

@RunWith(Parameterized.class)
public abstract class TestBase {
  
  @Parameters
  public static Collection<Object[]> params() {
    return Arrays.asList(
        new Object[]{new com.xebia.xoc.impl.javassist.MapperFactoryImpl()}, 
        new Object[]{new com.xebia.xoc.impl.beanutils.MapperFactoryImpl()});
  }

  protected final MapperFactory mapperFactory;

  public TestBase(MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }
  
}