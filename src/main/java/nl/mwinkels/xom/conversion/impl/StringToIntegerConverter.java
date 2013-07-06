package nl.mwinkels.xom.conversion.impl;

import nl.mwinkels.xom.conversion.ConversionException;
import nl.mwinkels.xom.conversion.Converter;

public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public boolean canConvert(Class<?> sourceClass, Class<?> targetCLass) {
        return sourceClass == String.class && targetCLass == Integer.class;
    }

    @Override
    public Integer convert(String value) throws ConversionException {
        return Integer.parseInt(value);
    }
}
