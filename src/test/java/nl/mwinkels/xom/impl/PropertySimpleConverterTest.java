package nl.mwinkels.xom.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.config.ClassMapperConfig;

public class PropertySimpleConverterTest extends TestBase {
  
  public PropertySimpleConverterTest(MapperFactory mapperFactory) {
    super(mapperFactory);
  }

  public static class Source {
    
    private Integer a;
    
    public Integer getA() {
      return a;
    }
    
    public void setA(Integer a) {
      this.a = a;
    }
  }
  
  public static class Target {
    
    private String b;
    
    public String getB() {
      return b;
    }
    
    public void setB(String b) {
      this.b = b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() throws MappingException {
    ClassMapperConfig config = new ClassMapperConfig().property("b").from("a").add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(12);
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(Integer.toString(source.getA())));
  }
  
}