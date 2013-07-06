package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.MappingException;

public interface ClassMapper<S, T> {

    T map(S source) throws MappingException;

    T map(S source, T target) throws MappingException;
}
