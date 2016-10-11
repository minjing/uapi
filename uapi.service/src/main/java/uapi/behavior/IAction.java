package uapi.behavior;

/**
 * The IAction is minimum data handling unit in Behavior framework.
 * It process input data and output processed data.
 *
 * @param   <I>
 *          Input data type
 * @param   <O>
 *          Output data type
 */
public interface IAction<I, O> {

    /**
     * Process input data and output processed data
     *
     * @param   input
     *          Inputted data
     * @return  Output data
     */
    O process(I input);
}
