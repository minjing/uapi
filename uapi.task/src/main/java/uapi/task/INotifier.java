package uapi.task;

public interface INotifier {

    void done();

    void failed(Throwable t);
}
