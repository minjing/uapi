/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import uapi.KernelException;
import uapi.config.ICliConfigProvider;
import uapi.helper.CollectionHelper;
import uapi.rx.Looper;
import uapi.service.IRegistry;
import uapi.service.IService;
import uapi.service.ITagged;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Bootstrap whole application
 */
public class Bootstrapper {

    private static final String[] basicSvcTags = new String[] {
        "Config", "Profile", "Log"
    };

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ServiceLoader<IService> svcLoaders = ServiceLoader.load(IService.class);
        final List<IRegistry> svcRegistries = new ArrayList<>();
        final List<IService> basicSvcs = new ArrayList<>();
        final List<IService> otherSvcs = new ArrayList<>();
        Looper.from(svcLoaders)
                .foreach(svc -> {
                    if (svc instanceof IRegistry) {
                        svcRegistries.add((IRegistry) svc);
                    } else if (svc instanceof ITagged) {
                        ITagged taggedSvc = (ITagged) svc;
                        String[] tags = taggedSvc.getTags();
                        if (CollectionHelper.contains(tags, basicSvcTags) != null) {
                            basicSvcs.add(svc);
                        } else {
                            otherSvcs.add(svc);
                        }
                    } else {
                        otherSvcs.add(svc);
                    }
                });

        if (svcRegistries.size() == 0) {
            throw new KernelException("A IRegistry must be provided");
        }
        if (svcRegistries.size() > 1) {
            throw new KernelException("Found multiple IRegistry instance {}", svcRegistries);
        }

        IRegistry svcRegistry = svcRegistries.get(0);
        // Register basic service first
        svcRegistry.register(basicSvcs.toArray(new IService[basicSvcs.size()]));
        svcRegistry = svcRegistry.findService(IRegistry.class);
        if (svcRegistry == null) {
            throw new KernelException("The service repository can't be satisfied");
        }

        // Parse command line parameters
        ICliConfigProvider cliCfgProvider = svcRegistry.findService(ICliConfigProvider.class);
        cliCfgProvider.parse(args);

        // Create profile
        ProfileManager profileMgr = svcRegistry.findService(ProfileManager.class);
        IProfile profile = profileMgr.getActiveProfile();

        // Register other service
        Looper.from(otherSvcs)
                .filter(profile::isAllow)
                .foreach(svcRegistry::register);

        svcRegistry.start();

        Launcher launcher = svcRegistry.findService(Launcher.class);
        launcher.launch(startTime);
    }
}
