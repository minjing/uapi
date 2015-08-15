package uapi.task;

/**
 * A task producer used to generate new task
 * 
 * @author min
 */
public interface ITaskProducer {

    /**
     * Inject task emitter which used to emit task to framework
     * 
     * @param taskEmitter   The task emitter which used to emit task to framework
     */
    void setEmitter(ITaskEmitter taskEmitter);
}
