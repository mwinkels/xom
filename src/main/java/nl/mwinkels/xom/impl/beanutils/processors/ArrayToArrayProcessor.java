package nl.mwinkels.xom.impl.beanutils.processors;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.ConversionException;

import java.lang.reflect.Array;

public class ArrayToArrayProcessor implements ValueProcessor {

    private final ValueProcessor valueProcessor;
    private final Class<?> targetComponentType;

    public ArrayToArrayProcessor(Class<?> targetComponentType, ValueProcessor valueProcessor) {
        this.targetComponentType = targetComponentType;
        this.valueProcessor = valueProcessor;
    }

    @Override
    public Object process(Object value) throws MappingException, ConversionException {
        Object[] objects = (Object[]) value;
        Object result = Array.newInstance(targetComponentType, objects.length);
        int i = 0;
        for (Object object : objects) {
            Array.set(result, i++, valueProcessor.process(object));
        }
        return result;
    }

}
