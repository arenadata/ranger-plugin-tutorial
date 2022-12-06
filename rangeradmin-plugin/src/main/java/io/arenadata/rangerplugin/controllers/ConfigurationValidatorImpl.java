package io.arenadata.rangerplugin.controllers;
import io.arenadata.rangerplugin.contracts.ConfigurationValidator;
import io.arenadata.rangerplugin.system.ManagedException;
import io.arenadata.rangerplugin.system.ManagedExceptionType;
import org.apache.ranger.plugin.client.BaseClient;
import java.util.HashMap;
import java.util.Map;

public final class ConfigurationValidatorImpl implements ConfigurationValidator {
    protected static final String RESOURCE_KEY  = "restapi.metadata.uri";
    private static final NaiveLoggerImpl logger = NaiveLoggerImpl.getInstance();
    private final HttpClientImpl httpClient     = new HttpClientImpl();

    public Map<String, Object> processServiceConfigs(String serviceName, Map<String, String> configs) throws ManagedException {
        if (serviceName == null || serviceName.trim().isEmpty())
            throw new ManagedException(ManagedExceptionType.INPUT_STRING_NULL_OR_EMPTY, "Can't process the configs for service, due the given service name is null or empty.");

        logger.writeLogMessage(String.format("Start processing the configs for service: '%s'", serviceName));

        if (!configs.containsKey(RESOURCE_KEY))
            throw new ManagedException(ManagedExceptionType.RESOURCE_KEY_ABSENCE, String.format("Given configuration doesn't contain next key '%s'", RESOURCE_KEY));

        boolean state = false;
        final Map<String, Object> responseData = new HashMap<>();

        try {
            for (final Map.Entry<String, String> entry : configs.entrySet()) {
                final String key   = entry.getKey();
                final String value = entry.getValue();
                logger.writeLogMessage(String.format("Reading the config key: '%s' and its value '%s'", key, value));

                switch (key) {
                    case RESOURCE_KEY:
                        state = httpClient.processTestRequest(value);
                        break;
                    default:
                        throw new ManagedException(ManagedExceptionType.INVALID_RESOURCE_KEY, "The given key is invalid for this Ranger plugin.");
                }
            }

            String responseMessage = state ? "Connection to the test REST API was successful" : "Failed, cannot connect to the test REST API";
            BaseClient.generateResponseDataMap(state, responseMessage, responseMessage, null, null, responseData);
        } catch (Exception exception) {
            logger.writeLogMessage(exception.toString());
        }

        return responseData;
    }
}
