/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.sample;

import uapi.annotation.NotNull;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
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
