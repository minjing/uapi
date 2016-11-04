package uapi.behavior;

import uapi.helper.Functionals;

/**
 * The builder is used to create IExecution instance
 */
public interface IExecutionBuilder {

    /**
     * Set current execution result evaluation
     *
     * @param   evaluator
     *          The evaluator
     * @return  This execution builder
     */
    IExecutionBuilder when(Functionals.Evaluator evaluator);

    /**
     * Set next action when current execution result evaluation is satisfied.
     *
     * @param   name
     *          The action name
     * @return  Next execution builder
     */
    IExecutionBuilder then(String name);

    /**
     * Build whole execution tree
     *
     * @return  The root of execution tree
     */
    IExecution build();
}
