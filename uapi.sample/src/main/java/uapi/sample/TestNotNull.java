package uapi.sample;

import uapi.annotation.NotNull;

/**
 * Created by min on 16/2/14.
 */
public class TestNotNull {

    public void sayHello(
            @NotNull final String name
    ) {
        System.out.print("Hello " + name);
    }
}
