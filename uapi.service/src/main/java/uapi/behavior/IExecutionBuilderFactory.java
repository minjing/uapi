package uapi.behavior;

/**
 * A factory for IExecutionBuilder instance creating
 */
public interface IExecutionBuilderFactory {

    /**
     * Create an IExecutionBuilder instance by an action
     *
     * @param   name
     *          The action name
     * @return  IExecutionBuilder instance
     */
    IExecutionBuilder from(String name);

//    /**
//     * Create an IExecutionBuilder instance by action or behavior
//     *
//     * @param   name
//     *          The name of action or behavior
//     * @param   isBehavior
//     *          Indicate the name is action or behavior
//     * @return  IExecutionBuilder instance
//     */
//    IExecutionBuilder from(String name, boolean isBehavior);
}
