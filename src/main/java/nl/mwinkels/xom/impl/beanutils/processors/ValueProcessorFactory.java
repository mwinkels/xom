package nl.mwinkels.xom.impl.beanutils.processors;

import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ClassMapper;
import nl.mwinkels.xom.impl.ClassMapperRegistry;
import nl.mwinkels.xom.impl.ConverterRegistry;
import org.apache.commons.lang3.ClassUtils;

/**
 * Factory class for {@link ValueProcessor}s for different cases.
 *
 * @author mwinkels@mwinkels.com
 */
public class ValueProcessorFactory {

    private final ConverterRegistry converterRegistry;
    private final ClassMapperRegistry mapperRegistry;

    public ValueProcessorFactory(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry) {
        this.converterRegistry = converterRegistry;
        this.mapperRegistry = mapperRegistry;
    }

    public ValueProcessor create(Class<?> sourceType, Class<?> targetType, ClassMapper<Object, Object> classMapper, Converter<Object, Object> converter) {
        ValueProcessor specialCase = createSpecialCases(sourceType, targetType, classMapper, converter);
        if (specialCase != null) {
            return specialCase;
        }
        if (classMapper != null) {
            return new ClassMapperProcessor(classMapper);
        }
        if (converter != null) {
            return new ConverterProcessor(converter);
        }
        boolean typesDiffer = !sourceType.isAssignableFrom(targetType);
        if (typesDiffer) {
            targetType = findPossibleWrapper(targetType);
            sourceType = findPossibleWrapper(sourceType);
            classMapper = (ClassMapper<Object, Object>) mapperRegistry.findClassMapper(sourceType, targetType);
            if (classMapper != null) {
                return new ClassMapperProcessor(classMapper);
            }
            converter = converterRegistry.findConverter(sourceType, targetType);
            if (converter != null) {
                return new ConverterProcessor(converter);
            }
        }
        return new SimpleProcessor();
    }

    private Class<?> findPossibleWrapper(Class<?> type) {
        if (type.isPrimitive()) {
            type = ClassUtils.primitiveToWrapper(type);
        }
        return type;
    }

    private ValueProcessor createSpecialCases(Class<?> sourceType, Class<?> targetType, ClassMapper<Object, Object> classMapper, Converter<Object, Object> converter) {
        if (sourceType.isArray()) {
            if (targetType.isArray()) {
                return new ArrayToArrayProcessor(targetType.getComponentType(), create(sourceType.getComponentType(), targetType.getComponentType(), classMapper, converter));
            }
        }
        return null;
    }

}
