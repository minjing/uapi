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
import uapi.service.annotation.Init;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage multiple service profiles
 */
@Service
@Tag("Profile")
class ProfileManager {

    private static final IProfile DEFAULT_PROFILE   = new EmptyProfile();

    @Config(path="cli.profile", optional=true)
    String _usedProfile;

    @Config(path="profiles", parser=ProfilesParser.class, optional=true)
    Map<String, IProfile> _profiles;

//    @Init
//    void init() {
//        if (Strings.isNullOrEmpty(this._usedProfile)) {
//            this._usedProfile = EmptyProfile.NAME;
//        }
//        if (this._profiles == null) {
//            this._profiles = new HashMap<>();
//        }
//        this._profiles.put(EmptyProfile.NAME, new EmptyProfile());
//    }

    IProfile getActiveProfile() {
        if (Strings.isNullOrEmpty(this._usedProfile)) {
            return DEFAULT_PROFILE;
        }
        IProfile profile = this._profiles.get(this._usedProfile);
        if (profile == null) {
            profile = DEFAULT_PROFILE;
        }
        return profile;
    }
}
