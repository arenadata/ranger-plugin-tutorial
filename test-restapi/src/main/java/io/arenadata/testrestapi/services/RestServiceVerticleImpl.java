package io.arenadata.testrestapi.services;
import io.arenadata.testrestapi.dao.Category;
import io.arenadata.testrestapi.repositories.CategoryRepositoryImpl;
import io.arenadata.testrestapi.system.CommonConstants;
import io.arenadata.testrestapi.system.ManagedException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RestServiceVerticleImpl extends AbstractVerticle {
    private static final int DEFAULT_HTTP_LISTENER_PORT  = 8080;
    private static final String DEFAULT_TEST_REST_METHOD = "get";
    private static final String DEFAULT_TEST_HTTP_HEADER = "test-ranger-user";
    private static final Map<String, String> REST_MAP    = new HashMap<>();

    static {
        REST_MAP.put(CommonConstants.DEFAULT_METADATA_URI_KEY ,"/api/v1/metadata");
        REST_MAP.put(CommonConstants.DEFAULT_DATA_URI_KEY     ,"/api/v1/data");
        REST_MAP.put(CommonConstants.DEFAULT_DATETIME_URI_KEY ,"/api/v1/datetime");
    }

    private static final Logger logger = LoggerFactory.getLogger(RestServiceVerticleImpl.class);
    private final AuthorizerServiceImpl authorizer  = new AuthorizerServiceImpl();
    private final CategoryRepositoryImpl repository = new CategoryRepositoryImpl();

    @Override
    public void start(Promise<Void> promise) throws ManagedException {
        logger.info("Started to initialize vertx router with HTTP-server...");
        final Router router = Router.router(vertx);
        router.get(REST_MAP.get(CommonConstants.DEFAULT_METADATA_URI_KEY)).handler(this::handleMetadataRequest);
        router.get(REST_MAP.get(CommonConstants.DEFAULT_DATA_URI_KEY)).handler(this::getTestData);
        router.get(REST_MAP.get(CommonConstants.DEFAULT_DATETIME_URI_KEY)).handler(this::handleDatetimeRequest);

        vertx.createHttpServer()
        .requestHandler(router)
        .listen(config().getInteger("http.port", DEFAULT_HTTP_LISTENER_PORT), result -> {
            if (result.succeeded())
                promise.complete();
            else
                promise.fail(result.cause());
        });

        seedTestData();
        authorizer.init();
    }

    private void seedTestData() throws ManagedException {
        logger.info("Started to seed data via Hibernate...");
        repository.startTransaction();
        repository.seedTestData();
        repository.endTransaction();
    }

    private Boolean checkIsRequestAuthorized(RoutingContext routingContext) {
        logger.info("Started to check the incoming authorization request...");
        final HttpServerRequest request    = routingContext.request();
        final String authorizationTestUser = request.getHeader(DEFAULT_TEST_HTTP_HEADER);
        final String absoluteUri           = request.absoluteURI();

        if (absoluteUri == null || absoluteUri.isEmpty()) {
            sendFailedStateResponse(routingContext, HttpResponseStatus.BAD_REQUEST.code());
            return false;
        }

        final String resourceValue = String.format("%s-uri", absoluteUri.replaceFirst(".*/([^/?]+).*","$1"));
        return handleRangerAuthorizationRequest(authorizationTestUser, resourceValue);
    }

    private boolean handleRangerAuthorizationRequest(String authorizationTestUser, String resourceValue) {
        final boolean isAuthorized = authorizer.handleAuthorization(
            REST_MAP.get(resourceValue),
            DEFAULT_TEST_REST_METHOD,
            authorizationTestUser,
            null
        );

        logger.info("Authorization response result: {}", isAuthorized);
        return isAuthorized;
    }

    private void sendSuccessfulResponseWithData(RoutingContext routingContext, Object data) {
        logger.info("Sending the successful response to client...");
        routingContext
        .response()
        .putHeader("content-type", "application/json")
        .setStatusCode(HttpResponseStatus.OK.code())
        .end(Json.encodePrettily(data));
    }

    private void sendFailedStateResponse(RoutingContext routingContext, int failedHttpStatusCode) {
        logger.error("Sending the failed state response to client...");
        routingContext
        .response()
        .putHeader("content-type", "application/json")
        .setStatusCode(failedHttpStatusCode)
        .end();
    }

    private void handleMetadataRequest(RoutingContext routingContext) {
        logger.info("Started to handle the metadata request...");
        Map<String, String> uriList = new HashMap<>(REST_MAP);
        uriList.remove(CommonConstants.DEFAULT_METADATA_URI_KEY);
        sendSuccessfulResponseWithData(routingContext, uriList);
    }

    private void getTestData(RoutingContext routingContext) {
        logger.info("Started to handle the data request, going to read data via JPA from the embedded H2 database...");
        final boolean isAuthorized = checkIsRequestAuthorized(routingContext);

        if (!isAuthorized)
            sendFailedStateResponse(routingContext, HttpResponseStatus.UNAUTHORIZED.code());
        else {
            final HttpServerRequest request = routingContext.request();
            final String offsetParamValue   = request.getParam(CommonConstants.DEFAULT_QUERY_PARAM_OFFSET_KEY);
            final String limitParamValue    = request.getParam(CommonConstants.DEFAULT_QUERY_PARAM_LIMIT_KEY);
            final List<Category> data       = repository.getAllWithOffsetAndLimit(
                offsetParamValue == null ? CommonConstants.DEFAULT_DATA_OFFSET_VALUE : processIntegerParameter(routingContext, offsetParamValue),
                limitParamValue  == null ? CommonConstants.DEFAULT_DATA_LIMIT_VALUE  : processIntegerParameter(routingContext, limitParamValue)
            );

            if (data == null || data.isEmpty())
                sendFailedStateResponse(routingContext, HttpResponseStatus.NOT_FOUND.code());

            sendSuccessfulResponseWithData(routingContext, data);
        }
    }

    private void handleDatetimeRequest(RoutingContext routingContext) {
        logger.info("Started to handle the datetime request...");
        final boolean isAuthorized = checkIsRequestAuthorized(routingContext);

        if (!isAuthorized)
            sendFailedStateResponse(routingContext, HttpResponseStatus.UNAUTHORIZED.code());
        else
            sendSuccessfulResponseWithData(routingContext, LocalDateTime.now());
    }

    private int processIntegerParameter(RoutingContext routingContext, String rawValue) {
        int parsedValue = CommonConstants.DEFAULT_INT_INVALID_VALUE;

        try {
            parsedValue = Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            sendFailedStateResponse(routingContext, HttpResponseStatus.BAD_REQUEST.code());
        }

        return parsedValue;
    }
}
