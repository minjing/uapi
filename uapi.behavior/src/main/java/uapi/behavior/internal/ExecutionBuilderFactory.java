package uapi.behavior.internal;

import uapi.KernelException;
import uapi.behavior.IAction;
import uapi.behavior.IBehaviorRepository;
import uapi.behavior.IExecutionBuilder;
import uapi.behavior.IExecutionBuilderFactory;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

/**
 * The implementation of IExecutionBuilderFactory
 */
@Service(IExecutionBuilderFactory.class)
@Tag("Behavior")
public class ExecutionBuilderFactory implements IExecutionBuilderFactory {

    @Inject
    protected IBehaviorRepository _behaviorRepo;

    @Override
    public IExecutionBuilder from(String name) {
        IAction action = this._behaviorRepo.find(name);
        if (action == null) {
            throw new KernelException("Can't find action/behavior {} in the repo", name);
        }
        return new Execution(this._behaviorRepo, action);
    }
}
