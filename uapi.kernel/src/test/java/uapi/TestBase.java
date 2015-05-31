package uapi;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.MockitoAnnotations;

@Ignore
public class TestBase {

    @Before
    public void before() {
        initMock();
    }

    private void initMock() {
        MockitoAnnotations.initMocks(this);
    }
}
