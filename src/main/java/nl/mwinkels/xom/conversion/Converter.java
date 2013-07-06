package nl.mwinkels.xom.conversion;

public interface Converter<S, T> {
  
  T convert(S value) throws ConversionException;
  
  boolean canConvert(Class<?> sourceClass, Class<?> targetCLass);
}
