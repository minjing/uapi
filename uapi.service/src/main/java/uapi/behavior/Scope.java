package uapi.behavior;

/**
 * The lifecycle for the data which is put in the execution context.
 */
public enum Scope {

    /**
     * Always keep the data until the context is destroyed
     */
    Global,

    /**
     * Keep the data only when the context go throw same behavior
     */
    Behavior
}
