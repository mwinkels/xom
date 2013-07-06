package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.config.ClassMapperConfig;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PropertyTest extends TestBase {

    public PropertyTest(MapperFactory mapperFactory) {
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
        ClassMapperConfig config = new ClassMapperConfig().property("b").from("a").add();
        ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
        assertThat(mapper, is(notNullValue()));
        Source source = new Source();
        source.setA(12);
        Target target = mapper.map(source);
        assertThat(target, is(notNullValue()));
        assertThat(target.getB(), is(source.getA()));
    }

}