package nl.mwinkels.xom.config;

/**
 * Configuration for a mapping a property.
 *
 * @param <C> The container type of this element.
 * @author mwinkels@mwinkels.com
 */
public class PropertyMapperConfig<C extends AbstractClassMapperConfig<?>> extends AbstractElementMapperConfig<C, PropertyMapperConfig<C>> {

    private String target;

    public PropertyMapperConfig(C classMapperConfig) {
        super(classMapperConfig);
    }

    public String getTarget() {
        return target;
    }

    protected void setTarget(String target) {
        this.target = target;
    }

    @Override
    protected PropertyMapperConfig<C> getThis() {
        return this;
    }

}
