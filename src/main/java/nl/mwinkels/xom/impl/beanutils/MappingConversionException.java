package nl.mwinkels.xom.impl.beanutils;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.ConversionException;

import java.util.List;

public class MappingConversionException extends MappingException {

    private List<ConversionException> conversionExceptions;

    public MappingConversionException() {
        super("ConversionErrors");
    }

    public void add(ConversionException e) {
        conversionExceptions.add(e);
    }
}
