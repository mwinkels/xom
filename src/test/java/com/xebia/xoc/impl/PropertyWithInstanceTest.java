package com.xebia.xoc.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.config.ClassMapperConfig;
import com.xebia.xoc.impl.ConfigurableMapper;
import com.xebia.xoc.impl.MapperFactory;

public class PropertyWithInstanceTest extends TestBase {
  
  public PropertyWithInstanceTest(MapperFactory mapperFactory) {
    super(mapperFactory);
  }

  public static class Source {
    
    private int a;
    
    public int getA() {
      return a;
    }
    
    public void setA(int a) {
      this.a = a;
    }
  }
  
  public static class Target {
    
    private int b;
    
    public int getB() {
      return b;
    }
    
    public void setB(int b) {
      this.b = b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() throws MappingException {
    ClassMapperConfig config = new ClassMapperConfig().property("b").from("a").add();
    ConfigurableMapper mapper = new ConfigurableMapper(mapperFactory);
    mapper.withMapper(config, Source.class, Target.class);
    Source source = new Source();
    source.setA(12);
    Target target = mapper.map(source, new Target());
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(source.getA()));
  }
  
}