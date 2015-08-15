package uapi.task;

/**
 * The tasks only can be executed one by one by its serial number
 * 
 * @author min
 */
public interface ISerialTask {

    /**
     * Get the serial id for this task
     * 
     * @return  The associated serial number of this task
     */
    String getSerialId();
}
