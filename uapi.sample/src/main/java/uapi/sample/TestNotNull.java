package uapi.sample;

import uapi.annotation.NotNull;
import uapi.injector.annotation.Inject;

import java.util.List;

/**
 * Created by min on 16/2/14.
 */
public class TestNotNull {

    @Inject
    protected String test;

    @Inject
    List<String> tests;

    public void sayHello(
            @NotNull final String name
    ) {
        System.out.print("Hello " + name);
    }
}
