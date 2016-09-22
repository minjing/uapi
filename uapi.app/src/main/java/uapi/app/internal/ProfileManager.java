/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.app.internal;

import com.google.common.base.Strings;
import uapi.config.annotation.Config;
import uapi.log.ILogger;
import uapi.service.IService;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.Map;

/**
 * Manage multiple service profiles
 */
@Service
@Tag("Profile")
class ProfileManager {

    static final IProfile DEFAULT_PROFILE   = new IncludeAllProfile();

    @Config(path="cli.profile", optional=true)
    protected String _usedProfile;

    @Config(path="profiles", parser=ProfilesParser.class, optional=true)
    protected Map<String, IProfile> _profiles;

    @Inject
    protected ILogger _logger;

    public IProfile getActiveProfile() {
        if (Strings.isNullOrEmpty(this._usedProfile)) {
            return DEFAULT_PROFILE;
        }
        IProfile profile = this._profiles.get(this._usedProfile);
        if (profile == null) {
            this._logger.warn("No profile is named {}, using default profile instead of", this._usedProfile);
            profile = DEFAULT_PROFILE;
        }
        return profile;
    }

    private static final class IncludeAllProfile implements IProfile {

        @Override
        public boolean isAllow(IService service) {
            return true;
        }
    }
}
