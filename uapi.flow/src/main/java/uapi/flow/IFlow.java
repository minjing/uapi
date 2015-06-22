package uapi.flow;

public interface IFlow {

    String getName();

    String[] bindedSourceNames();

    void bindSources(String[] sourceNames);
}
