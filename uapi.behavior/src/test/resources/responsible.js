{
    "name": "Test",
    "behaviors": [{
        "event": "event1",
        "behavior" = function(event) {
            print("handle event1 -> " + Object.prototype.toString.call(event));
        }
    }, {
        "event": "event2",
        "behavior" = function(event) {
            print("handle event2 -> " + Object.prototype.toString.call(event));
        }
    }]
}