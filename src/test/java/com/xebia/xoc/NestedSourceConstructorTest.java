package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;

public class NestedSourceConstructorTest extends TestBase {
  
  public NestedSourceConstructorTest(MapperFactory mapperFactory) {
    super(mapperFactory);
  }

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
    
    private final int b;
    
    public Target(int b) {
      this.b = b;
    }
    
    public int getB() {
      return b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() throws MappingException {
    ClassMapperConfig config = new ClassMapperConfig().constructorArg(0).from("nested.a").add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.getNested().setA(12);
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(source.getNested().getA()));
  }
  
}