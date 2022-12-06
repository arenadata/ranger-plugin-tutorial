package io.arenadata.rangerplugin.contracts;
import io.arenadata.rangerplugin.system.ManagedException;
import java.io.IOException;
import java.util.List;

public interface HttpClient {
    boolean processTestRequest(String url) throws ManagedException, IOException;
    List<String> getRestApiEndpoints(String url) throws ManagedException, IOException;
}
