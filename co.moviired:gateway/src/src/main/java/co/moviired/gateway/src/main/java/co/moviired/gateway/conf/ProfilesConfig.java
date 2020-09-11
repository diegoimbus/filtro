package co.moviired.gateway.conf;

import co.moviired.gateway.domain.dto.Operation;
import co.moviired.gateway.domain.dto.Profile;
import co.moviired.gateway.domain.dto.RedisProfileFields;
import co.moviired.gateway.domain.dto.UserProfiles;
import co.moviired.gateway.properties.SupportProfilesProperties;
import co.moviired.gateway.provider.RedisProvider;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for load in redis the profiles configuration from the profile service, this is made aiming to
 * load in cache profiles this way let a user request to advance or to get
 * blocked in the gateway
 **/

@Slf4j
@Component
public final class ProfilesConfig implements Serializable {

    private final RedisProvider redisProvider;
    private final SupportProfilesProperties profilesProperties;

    public ProfilesConfig(
            @NotNull RedisProvider predisProvider,
            SupportProfilesProperties pprofiles) {
        super();
        this.redisProvider = predisProvider;
        this.profilesProperties = pprofiles;
    }

    @PostConstruct
    public void loadProfiles() {
        UserProfiles userProfiles = getProfileObjectFromResponse(getProfileString());
        saveRedisProfiles(profileLogic(userProfiles));
    }

    /**
     * Initialize the profiles request in order to make
     * a http GET call and obtain an entity containing
     * all of the active profiles
     *
     * @return ResponseEntity<String> a responseEntity that
     * contains the response body of a JSON but in String format
     **/
    public ResponseEntity<String> getProfileString() {
        ResponseEntity<String> profileString = null;
        try {
            RestTemplate plantilla = new RestTemplate();
            profileString = ResponseEntity.ok(plantilla.getForObject(profilesProperties.getUrl(), String.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return profileString;
    }

    /**
     * Receives a {@link ResponseEntity}<String> that contains the profiles
     * representation if its not null it cast the JSON String to a
     * {@link UserProfiles}  class using GSON.
     *
     * @param responseEntity is the entity containing the String
     *                       representation of JSON object, to be parsed as  {@link UserProfiles}
     * @return {@link UserProfiles} a profile object with all existing profiles.
     **/
    public UserProfiles getProfileObjectFromResponse(ResponseEntity<String> responseEntity) {
        if (responseEntity == null) {
            throw new IllegalArgumentException("Error In getProfileObjectFromResponse responseEntity is null");
        }

        Gson g = new Gson();
        return g.fromJson(responseEntity.getBody(), UserProfiles.class);
    }

    public List<RedisProfileFields> profileLogic(UserProfiles userProfiles) {
        if (userProfiles == null) {
            throw new IllegalArgumentException("Error In profileLogic Method userProfiles are null");
        }

        List<RedisProfileFields> redisRoleFieldList = new ArrayList<>();
        RedisProfileFields redisProfileFields;
        for (Profile profile : userProfiles.getProfiles()) {
            redisProfileFields = new RedisProfileFields();
            redisProfileFields.setProfileName(profile.getProfileName());
            StringBuilder profilesPaths = new StringBuilder();
            List<Operation> operationList = profile.getOperations();
            for (int i = 0; i < operationList.size(); i++) {
                Operation operation = operationList.get(i);
                profilesPaths.append(operation.getOperationUrl());
                if (i < profile.getOperations().size() - 1) {
                    profilesPaths.append("|");
                }
            }
            redisProfileFields.setProfilePaths(profilesPaths);
            if (profilesPaths.length() > 0) {
                redisRoleFieldList.add(redisProfileFields);
            }
        }

        for (RedisProfileFields redisProfileFields1 : redisRoleFieldList) {
            log.info(redisProfileFields1.toString());
        }

        return redisRoleFieldList;
    }

    public void saveRedisProfiles(List<RedisProfileFields> redisFields) {
        if (redisFields == null) {
            throw new IllegalArgumentException("Error In saveRedisProfiles Method redisFields are null");
        }

        String loadedProfilesKey = "LOADED_MOVII_PROFILES";
        StringBuilder loadedProfiles = new StringBuilder();
        for (RedisProfileFields redisProfileFields : redisFields) {
            loadedProfiles.append(redisProfileFields.getProfileName());
            loadedProfiles.append(",");
            redisProvider.add(redisProfileFields.getProfileName(), redisProfileFields.getProfilePaths().toString());
        }
        redisProvider.add(loadedProfilesKey, loadedProfiles.toString());
    }

}

