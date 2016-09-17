package uapi.app.internal

import com.esotericsoftware.yamlbeans.YamlReader
import spock.lang.Specification
import uapi.KernelException

/**
 * Test case for ProfileParser
 */
class ProfileParserTest extends Specification {

    def 'Test parse'() {
        given:
        YamlReader reader = null;
        Map config;
        try {
            reader = new YamlReader(new FileReader('src/test/resources/profiles.yaml'));
            config = reader.read()
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        when:
        ProfilesParser parser = new ProfilesParser()
        Map<String, Profile> profiles = parser.parse(config.get('profiles'))

        then:
        profiles.size() == profileCount
        Profile profile1 = profiles.get(profile1Name)
        profile1 != null
        profile1.name == profile1Name
        profile1.model == profile1Model
        profile1.matching == profile1Matching
        profile1.tags.length == profile1TagCount
        profile1.tags[0] == p1t1
        profile1.tags[1] == p1t2

        Profile profile2 = profiles.get(profile2Name)
        profile2 != null
        profile2.name == profile2Name
        profile2.model == profile2Model
        profile2.matching == profile2Matching
        profile2.tags.length == profile2TagCount
        profile2.tags[0] == p2t1
        profile2.tags[1] == p2t2

        where:
        profileCount    | profile1Name  | profile2Name  | profile1Model         | profile2Model         | profile1Matching              | profile2Matching              | profile1TagCount  | profile2TagCount  | p1t1  | p1t2  | p2t1  | p2t2
        2               | '1'           | '2'           | Profile.Model.EXCLUDE | Profile.Model.INCLUDE | Profile.Matching.SATISFY_ALL  | Profile.Matching.SATISFY_ANY  | 2                 | 2                 | 'a'   | 'b'   | 'e'   | 'f'
    }

    def 'Test parse duplicated profile'() {
        given:
        YamlReader reader = null;
        Map config;
        try {
            reader = new YamlReader(new FileReader('src/test/resources/duplicated-profiles.yaml'));
            config = reader.read()
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        when:
        ProfilesParser parser = new ProfilesParser()
        parser.parse(config.get('profiles'))

        then:
        thrown(KernelException)
    }
}
