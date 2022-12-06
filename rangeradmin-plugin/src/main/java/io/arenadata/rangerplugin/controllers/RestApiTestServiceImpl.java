package io.arenadata.rangerplugin.controllers;
import org.apache.ranger.plugin.model.RangerService;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.service.RangerBaseService;
import org.apache.ranger.plugin.service.ResourceLookupContext;
import java.util.*;

public final class RestApiTestServiceImpl extends RangerBaseService {
    private static final NaiveLoggerImpl logger        = NaiveLoggerImpl.getInstance();
    private final ConfigurationValidatorImpl validator = new ConfigurationValidatorImpl();
    private final HttpClientImpl httpClient            = new HttpClientImpl();

    public RestApiTestServiceImpl() {
        super();
    }

    @Override
    public void init(RangerServiceDef serviceDef, RangerService service) {
        super.init(serviceDef, service);
    }

    @Override
    public Map<String, Object> validateConfig() throws Exception {
        return validator.processServiceConfigs(serviceName, configs);
    }

    @Override
    public List<String> lookupResource(ResourceLookupContext context) throws Exception {
        final String urlValue = configs.get(ConfigurationValidatorImpl.RESOURCE_KEY);
        logger.writeLogMessage(String.format("Fetched value '%s' for '%s' key, obtained by lookupResource() method", ConfigurationValidatorImpl.RESOURCE_KEY, urlValue));
        return httpClient.getRestApiEndpoints(urlValue);
    }
}
