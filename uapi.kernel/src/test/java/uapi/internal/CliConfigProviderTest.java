package uapi.internal;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uapi.config.IConfigTracer;
import uapi.test.TestBase;

public class CliConfigProviderTest extends TestBase {

    @Mock private IConfigTracer _cfgTracer;

    private CliConfigProvider _cliCfgProvder;

    @Before
    public void before() {
        super.before();
        this._cliCfgProvder = new CliConfigProvider();
        this._cliCfgProvder.setTracer(this._cfgTracer);
    }

    @Test
    public void testParseKeyValue() {
        this._cliCfgProvder.parse(new String[] { "-name=value" });

        verify(this._cfgTracer, times(1)).onChange("name", "value");
    }

    @Test
    public void testParseBooleanValue() {
        this._cliCfgProvder.parse(new String[] { "-is" });

        verify(this._cfgTracer, times(1)).onChange("is", "true");
    }

    @Test
    public void testParseMultipleValue() {
        this._cliCfgProvder.parse(new String[] { "-name=value", "-is", "key=value" });

        verify(this._cfgTracer, times(1)).onChange("name", "value");
        verify(this._cfgTracer, times(1)).onChange("is", "true");
        verify(this._cfgTracer, times(1)).onChange("key", "value");
    }
}
