package uapi.state;

/**
 * The operation contains data which will be used change state
 */
public interface IOperation {

    /**
     * The operation type
     *
     * @return  Operation type
     */
    String type();
}
