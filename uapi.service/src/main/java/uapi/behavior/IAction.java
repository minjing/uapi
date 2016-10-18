package uapi.behavior;

import uapi.IIdentifiable;

/**
 * The IAction is minimum data handling unit in Behavior framework.
 * It process input data and output processed data.
 *
 * @param   <I>
 *          Input data type
 * @param   <O>
 *          Output data type
 */
public interface IAction<I, O> extends IIdentifiable<String> {

    /**
     * Return the action name
     *
     * @return  Action name
     */
    default String name() {
        return getId();
    }

    /**
     * Process input data and output processed data
     *
     * @param   input
     *          Inputted data
     * @return  Output data
     */
    O process(I input);
}
