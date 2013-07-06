package nl.mwinkels.xom.impl.beanutils.processors;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.ConversionException;

public interface ValueProcessor {

    Object process(Object value) throws MappingException, ConversionException;
}
