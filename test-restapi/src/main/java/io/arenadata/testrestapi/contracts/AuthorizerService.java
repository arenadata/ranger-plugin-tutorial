package io.arenadata.testrestapi.contracts;
import java.util.Set;

public interface AuthorizerService {
    void init();
    boolean handleAuthorization(String path, String accessType, String user, Set<String> userGroups);
}
