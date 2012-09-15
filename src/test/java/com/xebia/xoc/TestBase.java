package com.xebia.xoc;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class TestBase {
  
  @Parameters
  public static Collection<Object[]> params() {
    return Arrays.asList(
        new Object[]{new com.xebia.xoc.javassist.MapperFactoryImpl()}, 
        new Object[]{new com.xebia.xoc.beanutils.MapperFactoryImpl()});
  }

  protected final MapperFactory mapperFactory;

  public TestBase(MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }
  
}