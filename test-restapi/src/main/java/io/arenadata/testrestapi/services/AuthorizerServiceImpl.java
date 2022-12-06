package io.arenadata.testrestapi.services;
import io.arenadata.testrestapi.contracts.AuthorizerService;
import org.apache.ranger.plugin.audit.RangerDefaultAuditHandler;
import org.apache.ranger.plugin.policyengine.RangerAccessRequest;
import org.apache.ranger.plugin.policyengine.RangerAccessRequestImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResourceImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResult;
import org.apache.ranger.plugin.service.RangerBasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

public final class AuthorizerServiceImpl implements AuthorizerService {
    private static final String DEFAULT_SERVICE_TYPE = "testrestapi";
    private static final String DEFAULT_APP_ID = "myrestapi";
    private static final String DEFAULT_PROTECTED_RESOURCE = "restendpoint";

    private static final Logger logger = LoggerFactory.getLogger(AuthorizerServiceImpl.class);
    private RangerBasePlugin plugin    = null;

    public synchronized void init() {
        logger.info("Going to initialize the client side of Ranger plugin...");

        if (plugin == null)
            plugin = new RangerBasePlugin(DEFAULT_SERVICE_TYPE, DEFAULT_APP_ID);

        plugin.setResultProcessor(new RangerDefaultAuditHandler());
        plugin.init();
    }

    public boolean handleAuthorization(String path, String accessType, String user, Set<String> userGroups) {
        logger.info("Going to handle the authorization request for user: {}, path: {}, access type: {}", user, path, accessType);
        final RangerAccessResourceImpl resource = new RangerAccessResourceImpl();
        resource.setValue(DEFAULT_PROTECTED_RESOURCE, path);

        final RangerAccessRequest request = new RangerAccessRequestImpl(resource, accessType, user, userGroups);
        final RangerAccessResult  result  = plugin.isAccessAllowed(request);
        return result != null && result.getIsAllowed();
    }
}
