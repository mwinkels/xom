package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.config.ClassMapperConfig;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NestedSourceAndTargetConstructorTest extends TestBase {

    public NestedSourceAndTargetConstructorTest(MapperFactory mapperFactory) {
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

        private final int b;

        public TargetNested(int b) {
            this.b = b;
        }

        public int getB() {
            return b;
        }

    }

    @Test
    public void shouldCreateMapper() throws MappingException {
        ClassMapperConfig config = new ClassMapperConfig()//
                .property("nested").from("nested").withMapper()//
                .constructorArg(0).from("a").add()//
                .add();
        ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
        assertThat(mapper, is(notNullValue()));
        Source source = new Source();
        source.getNested().setA(12);
        Target target = mapper.map(source);
        assertThat(target, is(notNullValue()));
        assertThat(target.getNested().getB(), is(source.getNested().getA()));
    }

}