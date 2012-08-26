package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;

public class PropertiesBooleanTest {
  
  public static class Source {
    
    private int a;
    
    private boolean b;
    
    public int getA() {
      return a;
    }
    
    public void setA(int a) {
      this.a = a;
    }
    
    public boolean isB() {
      return b;
    }
    
    public void setB(boolean b) {
      this.b = b;
    }
  }
  
  public static class Target {
    
    private int b;
    
    private boolean c;
    
    public int getB() {
      return b;
    }
    
    public void setB(int b) {
      this.b = b;
    }
    
    public boolean isC() {
      return c;
    }
    
    public void setC(boolean c) {
      this.c = c;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() {
    ClassMapperConfig config = new ClassMapperConfig()//
        .property("b").from("a").add()//
        .property("c").from("b").add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper().withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(12);
    source.setB(true);
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(source.getA()));
    assertThat(target.isC(), is(source.isB()));
  }
  
}
