package nl.mwinkels.xom.impl.beanutils.processors;

import nl.mwinkels.xom.conversion.ConversionException;
import nl.mwinkels.xom.conversion.Converter;

public class ConverterProcessor implements ValueProcessor {

    private final Converter<Object, Object> converter;

    public ConverterProcessor(Converter<Object, Object> converter) {
        this.converter = converter;
    }

    @Override
    public Object process(Object value) throws ConversionException {
        return converter.convert(value);
    }

}
