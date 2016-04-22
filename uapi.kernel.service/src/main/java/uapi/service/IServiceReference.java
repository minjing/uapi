package uapi.service;

import uapi.service.internal.QualifiedServiceId;

/**
 * Created by xquan on 3/22/2016.
 */
public interface IServiceReference {

    String getId();

    QualifiedServiceId getQualifiedId();

    Object getService();

    void notifySatisfied();
}
