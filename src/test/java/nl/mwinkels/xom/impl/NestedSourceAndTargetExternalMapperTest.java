package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.config.ClassMapperConfig;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NestedSourceAndTargetExternalMapperTest extends TestBase {

    public NestedSourceAndTargetExternalMapperTest(MapperFactory mapperFactory) {
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
        ConfigurableMapper configurableMapper = new ConfigurableMapper(mapperFactory);
        configurableMapper.withMapper(new ClassMapperConfig().property("b").from("a").add(), SourceNested.class, TargetNested.class);
        ClassMapperConfig config = new ClassMapperConfig()//
                .property("nested").from("nested")
                .add();
        ClassMapper<Source, Target> mapper = configurableMapper.withMapper(config, Source.class, Target.class);
        assertThat(mapper, is(notNullValue()));
        Source source = new Source();
        source.getNested().setA(12);
        Target target = mapper.map(source);
        assertThat(target, is(notNullValue()));
        assertThat(target.getNested().getB(), is(source.getNested().getA()));
    }

}