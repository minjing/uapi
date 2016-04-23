package uapi.app;

/**
 * The application launcher used to launch framework
 */
public interface ILauncher {

    /**
     * Launch the UAPI framework
     *
     * @param   startTime
     *          The timestamp of the application start time
     */
    void launch(long startTime);
}
