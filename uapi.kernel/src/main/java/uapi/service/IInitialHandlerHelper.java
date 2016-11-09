package uapi.service;

import uapi.annotation.ClassMeta;
import uapi.annotation.IBuilderContext;
import uapi.annotation.IHandlerHelper;

/**
 * The handler helper is used to help add specific method to the init method
 */
public interface IInitialHandlerHelper extends IHandlerHelper {

    String name = "InitialHelper";

    /**
     * Add new method to the init method.
     * The method must be no arguments and return nothing
     *
     * @param builderContext
     * @param classBuilder
     * @param methodNames
     */
    void addInitMethod(
            final IBuilderContext builderContext,
            final ClassMeta.Builder classBuilder,
            final String... methodNames);
}
