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

    private final Map<String, List<StatefulService>> _unSatisfiedServices;
    private final Map<String, List<StatefulService>> _satisfiedServices;

    public ServiceRepository() {
        this._unSatisfiedServices = new HashMap<>();
        this._satisfiedServices = new HashMap<>();
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

    IService getService(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getService(serviceType.getName());
    }

    IService getService(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        List<StatefulService> svcs = this._satisfiedServices.get(serviceId);
        if (svcs != null && svcs.size() == 1) {
            return svcs.get(0).getInstance();
        }
        svcs = this._unSatisfiedServices.get(serviceId);
        if (svcs == null) {
            throw new KernelException("Can't found specific service in the repository - {}", serviceId);
        }
        if (svcs.size() != 1) {
            throw new KernelException("Found more than one service associate with {}", serviceId);
        }
        return svcs.get(0).getInstance();
    }

    IService[] getServices(Class<?> serviceType) {
        if (serviceType == null) {
            throw new InvalidArgumentException("serviceType", InvalidArgumentType.EMPTY);
        }
        return getServices(serviceType.getName());
    }

    IService[] getServices(String serviceId) {
        if (Strings.isNullOrEmpty(serviceId)) {
            throw new InvalidArgumentException("serviceId", InvalidArgumentType.EMPTY);
        }
        List<StatefulService> svcs = this._satisfiedServices.get(serviceId);
        if (svcs != null) {
            return svcs.toArray(new IService[svcs.size()]);
        }
        svcs = this._unSatisfiedServices.get(serviceId);
        if (svcs == null) {
            return ArrayHelper.empty();
        }
        IService[] svcInsts = new IService[svcs.size()];
        for (int i = 0; i < svcs.size(); i++) {
            svcInsts[i] = svcs.get(i).getInstance();
        }
        return svcInsts;
    }
}
