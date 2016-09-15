package uapi.app.internal

import spock.lang.Specification
import uapi.log.ILogger

/**
 * Unit test for ProfileManager
 */
class ProfileManagerTest extends Specification {

    def 'Test getProfile'() {
        given:
        def logger = Mock(ILogger)
        def profile = Mock(IProfile)
        ProfileManager profileMgr = new ProfileManager()
        profileMgr._logger = logger;
        profileMgr._profiles = new HashMap<>()
        profileMgr._profiles.put('profile1', profile)
        profileMgr._usedProfile = 'profile1'

        expect:
        profileMgr.getActiveProfile() == profile
    }

    def 'Test getProfile with default'() {
        given:
        def logger = Mock(ILogger)
        def profile = Mock(IProfile)
        ProfileManager profileMgr = new ProfileManager()
        profileMgr._logger = logger;
        profileMgr._profiles = new HashMap<>()
        profileMgr._profiles.put('profile1', profile)

        expect:
        profileMgr.getActiveProfile() != profile
        profileMgr.getActiveProfile() == profileMgr.DEFAULT_PROFILE
    }

    def 'Test getProfile with incorrect profile name'() {
        given:
        def logger = Mock(ILogger) {
            2 * warn(_, _)
        }
        def profile = Mock(IProfile)
        ProfileManager profileMgr = new ProfileManager()
        profileMgr._logger = logger;
        profileMgr._profiles = new HashMap<>()
        profileMgr._profiles.put('profile1', profile)
        profileMgr._usedProfile = 'profile2'

        expect:
        profileMgr.getActiveProfile() != profile
        profileMgr.getActiveProfile() == profileMgr.DEFAULT_PROFILE
    }
}
