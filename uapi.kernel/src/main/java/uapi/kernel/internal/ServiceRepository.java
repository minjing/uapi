package uapi.kernel.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

import uapi.kernel.IService;
import uapi.kernel.InvalidArgumentException;
import uapi.kernel.InvalidArgumentException.InvalidArgumentType;
import uapi.kernel.KernelException;
import uapi.kernel.helper.ArrayHelper;

public class ServiceRepository {

    private final Map<String, List<StatefulService>>    _unSatisfiedServices;
    private final Map<String, List<StatefulService>>    _satisfiedServices;
    private final Map<String, List<Object>>             _extenalServices;

    public ServiceRepository() {
        this._unSatisfiedServices = new HashMap<>();
        this._satisfiedServices = new HashMap<>();
        this._extenalServices = new HashMap<>();
    }

    public void addExtenalService(String sid, Object instance) {
        if (Strings.isNullOrEmpty(sid)) {
            throw new InvalidArgumentException("sid", InvalidArgumentType.EMPTY);
        }
        if (instance == null) {
            throw new InvalidArgumentException("instance", InvalidArgumentType.EMPTY);
        }
        List<Object> svcs = this._extenalServices.get(sid);
        if (svcs == null) {
            svcs = new ArrayList<>();
            this._extenalServices.put(sid, svcs);
        }
        svcs.add(instance);
    }

    public void addService(IService service) {
        if (service == null) {
            throw new InvalidArgumentException("service", InvalidArgumentType.EMPTY);
        }
        StatefulService svc = new StatefulService(this, service);
        List<StatefulService> svcs = this._unSatisfiedServices.get(svc.getId());
        if (svcs == null) {
            svcs = new ArrayList<>();
            this._unSatisfiedServices.put(svc.getId(), svcs);
        }
        svcs.add(svc);
    }

    Object getService(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getService(serviceType.getName());
    }

    Object getService(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        List<Object> extenalSvcs = this._extenalServices.get(serviceId);
        if (extenalSvcs != null) {
            
        }
        List<StatefulService> svcs = this._satisfiedServices.get(serviceId);
        if (svcs != null && svcs.size() == 1) {
            return svcs.get(0).getInstance();
        }
        svcs = this._unSatisfiedServices.remove(serviceId);
        if (svcs == null) {
            throw new KernelException("Can't found specific service in the repository - {}", serviceId);
        }
        if (svcs.size() != 1) {
            throw new KernelException("Found more than one service associate with {}", serviceId);
        }
        StatefulService svc = svcs.get(0);
        Object svcInst = svc.getInstance();
        addSatisfiedService(svc);
        return svcInst;
    }

    Object[] getServices(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getServices(serviceType.getName());
    }

    Object[] getServices(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        List<StatefulService> svcs = this._satisfiedServices.get(serviceId);
        if (svcs != null) {
            return svcs.toArray(new IService[svcs.size()]);
        }
        svcs = this._unSatisfiedServices.remove(serviceId);
        if (svcs == null) {
            return ArrayHelper.empty();
        }
        Object[] svcInsts = new IService[svcs.size()];
        for (int i = 0; i < svcs.size(); i++) {
            StatefulService svc = svcs.get(i);
            svcInsts[i] = svc.getInstance();
            addSatisfiedService(svc);
        }
        return svcInsts;
    }

    private void addSatisfiedService(StatefulService service) {
        List<StatefulService> svcs = this._satisfiedServices.get(service.getId());
        if (svcs == null) {
            svcs = new ArrayList<>();
            this._satisfiedServices.put(service.getId(), svcs);
        }
        svcs.add(service);
    }
}
