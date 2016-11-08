uapi.behavior.internal.ExecutionContext context = new uapi.behavior.internal.ExecutionContext(this._eventBus);
        java.util.Map data = event.attachment(uapi.behavior.IEventDrivenBehavior.KEY_EVENT_CONTEXT);
        if (data != null) {
            context.put(data, uapi.behavior.Scope.Global);
            event.clearAttachment(uapi.behavior.IEventDrivenBehavior.KEY_EVENT_CONTEXT);
        }
        /*
        uapi.event.IEvent rawEvent = event;
        if (event instanceof uapi.behavior.internal.BehaviorEvent) {
            uapi.behavior.internal.BehaviorEvent bEvent = (uapi.behavior.internal.BehaviorEvent) event;
            context.put(bEvent.data(), Scope.Global);
            rawEvent = bEvent.rawEvent();
        }*/
        process(event, context);