package com.xebia.xoc.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.config.ClassMapperConfig;

public class ArrayPropertyTest extends TestBase {
  
  public ArrayPropertyTest(MapperFactory mapperFactory) {
    super(mapperFactory);
  }

  public static class Source {
    
    private Integer[] a;
    
    public Integer[] getA() {
      return a;
    }
    
    public void setA(Integer[] a) {
      this.a = a;
    }
  }
  
  public static class Target {
    
    private String[] b;
    
    public String[] getB() {
      return b;
    } 
    
    public void setB(String b[]) {
      this.b = b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() throws MappingException {
    ClassMapperConfig config = new ClassMapperConfig().property("b").from("a").add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(new Integer[]{12,14});
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(new String[]{"12", "14"}));
  }
  
}