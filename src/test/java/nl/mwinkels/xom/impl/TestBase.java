package nl.mwinkels.xom.impl;

import java.util.Arrays;
import java.util.Collection;

import nl.mwinkels.xom.impl.beanutils.MapperFactoryImpl;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class TestBase {
  
  @Parameters
  public static Collection<Object[]> params() {
    return Arrays.asList(
        new Object[]{new nl.mwinkels.xom.impl.javassist.MapperFactoryImpl()},
        new Object[]{new MapperFactoryImpl()});
  }

  protected final MapperFactory mapperFactory;

  public TestBase(MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }
  
}