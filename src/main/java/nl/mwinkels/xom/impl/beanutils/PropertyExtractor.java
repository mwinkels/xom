package nl.mwinkels.xom.impl.beanutils;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.conversion.ConversionException;
import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ClassMapperBuilder;
import nl.mwinkels.xom.impl.beanutils.processors.ValueProcessorFactory;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

public class PropertyExtractor extends AbstractElementExtractor {

    private final String target;

    public PropertyExtractor(String source, String target, Converter<Object, Object> converter, ClassMapperBuilder nestedClassMapperBuilder) {
        super(source, converter, nestedClassMapperBuilder);
        this.target = target;
    }

    public void apply(Object source, Object target, ValueProcessorFactory valueProcessorFactory) throws MappingException, ConversionException {
        try {
            Class<?> targetType = PropertyUtils.getPropertyType(target, this.target);
            Object value = extractValue(source, targetType, valueProcessorFactory);
            PropertyUtils.setProperty(target, this.target, value);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        } catch (NoSuchMethodException e) {
            throw new MappingException(e);
        } catch (MappingException e) {
            throw new MappingException(e);
        }
    }

}
