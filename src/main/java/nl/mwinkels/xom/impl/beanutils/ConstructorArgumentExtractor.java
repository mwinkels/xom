package nl.mwinkels.xom.impl.beanutils;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.ConversionException;
import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ClassMapperBuilder;
import nl.mwinkels.xom.impl.beanutils.processors.ValueProcessorFactory;

public class ConstructorArgumentExtractor extends AbstractElementExtractor {

    public ConstructorArgumentExtractor(String source, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
        super(source, converter, nestedClassMapperBuilder);
    }

    public Object apply(Object source, Class<?> targetType, ValueProcessorFactory valueProcessorFactory) throws MappingException, ConversionException {
        return extractValue(source, targetType, valueProcessorFactory);
    }

}
