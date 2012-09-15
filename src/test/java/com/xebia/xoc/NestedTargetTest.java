package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;

public class NestedTargetTest extends TestBase {
  
  public NestedTargetTest(MapperFactory mapperFactory) {
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
    
    private TargetNested nested;
    
    public TargetNested getNested() {
      return nested;
    }
    
    public void setNested(TargetNested nested) {
      this.nested = nested;
    }
  }
  
  public static class TargetNested {
    
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
    ClassMapperConfig config = new ClassMapperConfig()//
        .property("nested").withMapper()//
          .property("b").from("a").add()//
        .add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(12);
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getNested().getB(), is(source.getA()));
  }
  
}