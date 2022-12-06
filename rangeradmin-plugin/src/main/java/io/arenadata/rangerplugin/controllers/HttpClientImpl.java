package io.arenadata.rangerplugin.controllers;
import io.arenadata.rangerplugin.contracts.HttpClient;
import io.arenadata.rangerplugin.system.ManagedException;
import io.arenadata.rangerplugin.system.ManagedExceptionType;
import org.apache.commons.io.IOUtils;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class HttpClientImpl implements HttpClient {
    private static final int DEFAULT_HTTP_SUCCESS_CODE = 200;
    private static final NaiveLoggerImpl logger = NaiveLoggerImpl.getInstance();

    public boolean processTestRequest(String url) throws ManagedException, IOException {
        if (url == null || url.trim().isEmpty())
            throw new ManagedException(ManagedExceptionType.INPUT_STRING_NULL_OR_EMPTY, "Can't process the test HTTP-request, due the given url is null or empty.");

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(url);

            try (final CloseableHttpResponse response = httpclient.execute(httpGet)) {
                for (final Header header : response.getAllHeaders())
                    logger.writeLogMessage(String.format("HTTP header: %s%nHeader value: %s",header.getName(), header.getValue()));

                final StatusLine line = response.getStatusLine();
                final int statusCode  = line.getStatusCode();
                return statusCode == DEFAULT_HTTP_SUCCESS_CODE;
            }
        }
    }

    public List<String> getRestApiEndpoints(String url) throws ManagedException, IOException {
        if (url == null || url.trim().isEmpty())
            throw new ManagedException(ManagedExceptionType.INPUT_STRING_NULL_OR_EMPTY, "Can't process the test HTTP-request, due the given url is null or empty.");

        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(url);
            logger.writeLogMessage(String.format("Going to fetch the REST endpoints list via next URL: %s", url));

            try (final CloseableHttpResponse response = httpclient.execute(httpGet)) {
                final HttpEntity entity       = response.getEntity();
                final InputStream inputStream = entity.getContent();
                final String result           = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                logger.writeLogMessage(String.format("Response data: %s", result));

                final ObjectMapper mapper = new ObjectMapper();
                final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() { };
                final Map<String, String> map = mapper.readValue(result, typeRef);
                return new ArrayList<>(map.values());
            }
        }
    }
}
