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

import javax.script.*;
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

    @Inject
    protected ActionRepository _actionRepo;

    @Inject
    protected BehaviorRepository _behaviorRepo;

    public void init() {
        // Load js based responsible if the config is specified
        if (this._behaviorDefPath != null) {
            File dir = new File(this._behaviorDefPath);
            if (!dir.exists()) {
                throw new KernelException("The behavior definition directory is not exist - {}", this._behaviorDefPath);
            }
            if (!dir.isDirectory()) {
                throw new KernelException("The behavior definition directory is not a directory - {}", this._behaviorDefPath);
            }

            // Initial javascript engine
            ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
            Bindings bindings = jsEngine.createBindings();
            bindings.put("registry", this);
            bindings.put("actionRepo", this._actionRepo);
            bindings.put("behaviorRepo", this._behaviorRepo);
            jsEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

            // Load all js file
            File[] jsFiles = dir.listFiles(file -> file.getName().endsWith(".js"));
            Looper.from(jsFiles).foreach(jsFile -> {
                try {
                    jsEngine.eval(new FileReader(jsFile));
                } catch (IOException | ScriptException ex) {
                    this._logger.error(ex);
                }
            });
        }

        // Register behavior/event handler into event bus
        Looper.from(this._responsibles)
                .flatmap(responsible -> Looper.from(responsible.behaviors()))
                .foreach(behavior -> this._eventBus.register(behavior));
    }

    public void register(ScriptObjectMirror mirror) {
        // Todo: invoked from javascript
    }
}
