/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.test;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.MockitoAnnotations;

@Ignore
public class MockitoTest {

    @Before
    public void before() {
        initMock();
    }

    private void initMock() {
        MockitoAnnotations.initMocks(this);
    }
}
