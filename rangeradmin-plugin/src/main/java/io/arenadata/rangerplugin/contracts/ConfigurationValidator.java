package io.arenadata.rangerplugin.contracts;
import io.arenadata.rangerplugin.system.ManagedException;
import java.util.Map;

public interface ConfigurationValidator {
    Map<String, Object> processServiceConfigs(String serviceName, Map<String, String> configs) throws ManagedException;

}
