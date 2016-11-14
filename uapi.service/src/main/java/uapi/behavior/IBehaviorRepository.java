package uapi.behavior;

/**
 * The repository used to store and receive Action or Behavior
 */
public interface IBehaviorRepository {

    /**
     * Register a new behavior to the repository
     *
     * @param   behavior
     *          Registered behavior
     */
    void register(IBehavior behavior);

    /**
     * Register a new action to the repository
     *
     * @param   action
     *          The action which will be registered in
     */
    void register(IAction action);

    IAction find(String name);

//    /**
//     * Find specific Action by name
//     *
//     * @param   name
//     *          Specific action name
//     * @return  Matched Action or null
//     */
//    IAction findAction(final String name);
//
//    /**
//     * Find specific Behavior by name
//     *
//     * @param   name
//     *          Specific Behavior name
//     * @return  Matched Behavior or null
//     */
//    IBehavior findBehavior(String name);
}
