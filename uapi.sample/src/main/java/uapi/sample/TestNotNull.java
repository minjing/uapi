package uapi.sample;

import uapi.annotation.NotNull;
import uapi.injector.annotation.Inject;
import uapi.service.annotation.Init;
import uapi.service.annotation.Service;

import java.util.List;

/**
 * Created by min on 16/2/14.
 */
@Service
public class TestNotNull {

    @Inject
    protected String test;

    @Inject("test.string")
    List<String> tests;

    public void sayHello(
            @NotNull final String name
    ) {
        System.out.print("Hello " + name);
    }

    @Init
    public void init2() {

    }
}
