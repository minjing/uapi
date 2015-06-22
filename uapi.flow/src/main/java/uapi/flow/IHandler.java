package uapi.flow;

/**
 * The IHandler present that it can handle input data and
 * generate an output data.
 * 
 * @author min
 *
 * @param <IT> The input data type
 * @param <OT> The output data type
 */
public interface IHandler<IT, OT> {

    /**
     * The handler name.
     * 
     * @return The handler name
     */
    String getName();

    OT handle(IT input, IContext context);
}
