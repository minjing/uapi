package uapi.sample.behavior;

import uapi.behavior.BehaviorEvent;

/**
 * Created by xquan on 11/14/2016.
 */
public class CounterIncEvent extends BehaviorEvent {

    public static final String TOPIC    = "CounterInc";

    public CounterIncEvent() {
        super(TOPIC);
    }
}
