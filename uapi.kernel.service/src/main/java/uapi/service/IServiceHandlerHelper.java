package uapi.service;

import uapi.annotation.ClassMeta;
import uapi.annotation.IHandlerHelper;

/**
 * A helper for maintain service annotation at build-time
 */
public interface IServiceHandlerHelper extends IHandlerHelper {

    String name = "ServiceHelper";

    void addServiceId(ClassMeta.Builder classBuilder, String... serviceIds);
}
