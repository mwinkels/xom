package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.config.ClassMapperConfig;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

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
