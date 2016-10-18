package uapi.behavior.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import uapi.KernelException;
import uapi.behavior.IResponsible;
import uapi.config.annotation.Config;
import uapi.event.IEventBus;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Read js files and generate Responsible
 */
@Service
public class ResponsibleRegistry {

    @Config(path="path.behavior", optional=true)
    protected String _behaviorDefPath;

    @Inject
    protected ILogger _logger;

    @Inject
    protected List<IResponsible> _responsibles = new LinkedList<>();

    @Inject
    protected IEventBus _eventBus;

    public void init() {
        if (this._behaviorDefPath == null) {
            return;
        }
        File dir = new File(this._behaviorDefPath);
        if (! dir.exists()) {
            throw new KernelException("The behavior definition directory is not exist - {}", this._behaviorDefPath);
        }
        if (! dir.isDirectory()) {
            throw new KernelException("The behavior definition directory is not a directory - {}", this._behaviorDefPath);
        }
        File[] jsFiles = dir.listFiles(file -> file.getName().endsWith(".js"));
        ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
        Looper.from(jsFiles).foreach(jsFile -> {
            try {
                jsEngine.eval(new FileReader(jsFile));
            } catch (IOException | ScriptException ex) {
                this._logger.error(ex);
            }
        });

        // Register behavior/event handler into event bus
        Looper.from(this._responsibles)
                .flatmap(responsible -> Looper.from(responsible.behaviors()))
                .foreach(behavior -> this._eventBus.register(behavior));
    }

    public void register(ScriptObjectMirror mirror) {
        // Todo: invoked from javascript
    }
}
