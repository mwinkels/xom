package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.NestedSourceAndTargetTest.SourceNested;
import com.xebia.xoc.config.ClassMapperConfig;

public class NestedSourceAndTargetConstructorTest {
  
  public static class Source {
    
    private SourceNested nested = new SourceNested();
    
    public SourceNested getNested() {
      return nested;
    }
    
  }
  
  public static class SourceNested {
    
    private int a;
    
    public int getA() {
      return a;
    }
    
    public void setA(int a) {
      this.a = a;
    }
  }
  
  public static class Target {
    
    private TargetNested nested;
    
    public TargetNested getNested() {
      return nested;
    }
    
    public void setNested(TargetNested nested) {
      this.nested = nested;
    }
  }
  
  public static class TargetNested {
    
    private final int b;
    
    public TargetNested(int b) {
      this.b = b;
    }
    
    public int getB() {
      return b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() {
    ClassMapperConfig config = new ClassMapperConfig()//
        .property("nested").from("nested").withMapper()//
          .constructorArg(0).from("a").add()//
        .add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper().withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.getNested().setA(12);
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getNested().getB(), is(source.getNested().getA()));
  }
  
}