package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;

public class SimpleTest extends TestBase {
  
  public static class Source {
    
  }
  
  public static class Target {
    
  }

  public SimpleTest(MapperFactory mapperFactory) {
    super(mapperFactory);
  }
  
  @Test
  public void shouldCreateMapper() throws MappingException {
    ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(new ClassMapperConfig(), Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Target target = mapper.map(new Source());
    assertThat(target, is(notNullValue()));
  }

}
