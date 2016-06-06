package uapi.annotation;

/**
 * A Handler to make outside handler and extend or modify specific handler's model.
 */
public interface IHandlerHelper {

    /**
     * Return the name of the helper
     *
     * @return  The name of the helper
     */
    String getName();
}
