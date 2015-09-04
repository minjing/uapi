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
