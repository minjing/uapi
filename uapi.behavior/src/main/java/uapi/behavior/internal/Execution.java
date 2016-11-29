package uapi.behavior.internal;

import uapi.InvalidArgumentException;
import uapi.KernelException;
import uapi.behavior.*;
import uapi.helper.ArgumentChecker;
import uapi.helper.Functionals;
import uapi.helper.IAttributed;
import uapi.helper.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IExecution
 */
public class Execution implements IExecution, IExecutionBuilder {

    private final IBehaviorRepository _behaviorRepo;

    private final IAction _action;

    private final Execution _parent;

    private final List<Pair<Functionals.Evaluator, Execution>> _conditionalChildren = new ArrayList<>();

    private Execution _defaultChild;

    private Class<?> _behaviorOutputType;

    private boolean _built = false;

    Execution(
            final IBehaviorRepository behaviorRepo,
            final IAction action
    ) {
        this(behaviorRepo, action, null);
    }

    Execution(
            final IBehaviorRepository behaviorRepo,
            final IAction action,
            final Execution parent
    ) {
        ArgumentChecker.required(behaviorRepo, "behaviorRepo");
        ArgumentChecker.required(action, "action");

        this._behaviorRepo = behaviorRepo;
        this._action = action;
        this._parent = parent;
    }

    @Override
    public Object execute(Object data, IExecutionContext context) {
        ArgumentChecker.required(data, "data");
        ArgumentChecker.required(context, "context");
        if (!this._built) {
            throw new KernelException("The execution is not built");
        }

        return this._action.process(data, context);
    }

    @Override
    public IExecution next(Object data) {
        if (! hasChild()) {
            return null;
        }

        IAttributed result = (IAttributed) data;
        Execution execution = null;
        for (Pair<Functionals.Evaluator, Execution> conditionalChild : this._conditionalChildren) {
            Functionals.Evaluator evaluator = conditionalChild.getLeftValue();
            if (evaluator.accept(result)) {
                execution = conditionalChild.getRightValue();
                break;
            }
        }
        if (execution == null) {
            execution = this._defaultChild;
        }
        return execution;
    }

    private Functionals.Evaluator _currentEval;

    @Override
    public IExecutionBuilder when(Functionals.Evaluator evaluator) {
        ArgumentChecker.required(evaluator, "evaluator");
        if (this._built) {
            throw new KernelException("The execution is built");
        }
        if (this._currentEval != null) {
            throw new KernelException("The current evaluator is not used");
        }
        this._currentEval = evaluator;
        return this;
    }

    @Override
    public IExecutionBuilder then(String name) {
        ArgumentChecker.required(name, "name");
        if (this._built) {
            throw new KernelException("The execution is built");
        }
        IAction action = this._behaviorRepo.find(name);
        if (action == null) {
            throw new InvalidArgumentException("The specific action can't be found in repo - {}", name);
        }

        if (this._currentEval == null) {
            this._defaultChild = new Execution(this._behaviorRepo, action, this);
            return this._defaultChild;
        } else {
            Execution next = new Execution(this._behaviorRepo, action, this);
            this._conditionalChildren.add(new Pair<>(this._currentEval, next));
            this._currentEval = null;
            return next;
        }
    }

    @Override
    public IExecution build() {
        if (this._built) {
            throw new KernelException("The execution is built");
        }

        Execution root = this;
        while (root._parent != null) {
            root = root._parent;
        }

        root.verify();

        // Check behavior output type
        List<Execution> leafChildren = new ArrayList<>();
        root.findLeafChildren(leafChildren);
        Class<?> behaviorOutputType = leafChildren.get(0).outputType();
        for (Execution child : leafChildren) {
            if (! child.outputType().equals(behaviorOutputType)) {
                throw new KernelException("The leaf execution output is not matched - {} vs. {}",
                        child.outputType(), behaviorOutputType);
            }
        }
        this._behaviorOutputType = behaviorOutputType;
        this._built = true;

        return root;
    }

    private void verify() {
        // Verify type
        if (this._parent == null) {
            return;
        }
        if (! this._parent.outputType().equals(this._action.inputType())) {
            throw new KernelException("The output type of {} is not matched input type of {}",
                    this._parent._action.name(), this._action.name());
        }
        // Verify condition
        if (this._conditionalChildren.size() > 0 && this._defaultChild == null) {
            throw new KernelException("The default branch must be specified if it defines some condition");
        }

        if (this._defaultChild != null) {
            this._defaultChild.verify();
        }
        for (Pair<Functionals.Evaluator, Execution> conditionalChild : this._conditionalChildren) {
            Execution child = conditionalChild.getRightValue();
            child.verify();
        }
    }

    private void findLeafChildren(List<Execution> children) {
        if (! hasChild()) {
            children.add(this);
            return;
        }
        if (this._defaultChild != null) {
            this._defaultChild.findLeafChildren(children);
        }
        for (Pair<Functionals.Evaluator, Execution> conditionalChild : this._conditionalChildren) {
            Execution child = conditionalChild.getRightValue();
            child.findLeafChildren(children);
        }
    }

    private boolean hasChild() {
        return this._defaultChild != null || this._conditionalChildren.size() > 0;
    }

    private Class<?> inputType() {
        return this._action.inputType();
    }

    private Class<?> outputType() {
        return this._action.outputType();
    }
}
