package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;

public class ArrayPropertySameTypeTest {
  
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
    
    private Integer[] b;
    
    public Integer[] getB() {
      return b;
    } 
    
    public void setB(Integer b[]) {
      this.b = b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() {
    ClassMapperConfig config = new ClassMapperConfig().property("b").from("a").add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper().withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(new Integer[]{12,14});
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(source.getA()));
    assertThat(target.getB(), is(not(sameInstance(source.getA()))));
  }
  
}