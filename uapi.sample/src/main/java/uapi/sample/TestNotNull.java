package uapi.sample;

import uapi.annotation.NotNull;
import uapi.injector.annotation.Inject;

/**
 * Created by min on 16/2/14.
 */
public class TestNotNull {

    @Inject
    private String test;

    public void sayHello(
            @NotNull final String name
    ) {
        System.out.print("Hello " + name);
    }
}
