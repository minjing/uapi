uapi.behavior.IExecution execution = this.${executionField};
        while (execution != null) {
            Object result = execution.execute(input, context);
            execution = execution.next(result);
        }

        // Return nothing
        return null;