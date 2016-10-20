// Available variables:
// registry -> ResponsibleRegistry
// actionRepo -> IActionRepository
// behaviorRepo -> IBehaviorRepository

var responsible = new uapi.behavior.IResponsilble({
    name: function() {
        return 'Test';
    },
    behaviors: function() {
        return [
            new uapi.behavior.IEventBasedBehavior({
                topic: function() {
                    return "event1";
                },
                handle: function(event) {
                    print("handle event1 -> " + Object.prototype.toString.call(event));
                }
            }),
            new uapi.behavior.IEvenBasedBehavior({
                topic: function() {
                    return "event2"
                },
                handle: function(event) {
                    print("handle event2 -> " + Object.prototype.toString.call(event));
                }
             })
        ]
    }
});

registry.register(responsible);