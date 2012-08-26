package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;

public class MapperBuilderSimpleTest {
	
	public static class Source {
		
	}
	
	public static class Target {
		
	}

	@Test
	public void shouldCreateMapper() {
		ClassMapper<Source, Target> mapper = new ConfigurableMapper().withMapper(new ClassMapperConfig(), Source.class, Target.class);
		assertThat(mapper, is(notNullValue()));
		Target target = mapper.map(new Source());
		assertThat(target, is(notNullValue()));
	}
	
}
