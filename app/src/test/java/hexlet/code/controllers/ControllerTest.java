package hexlet.code.controllers;

import hexlet.code.App;
import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ControllerTest {

    protected static Javalin app;
    protected static String baseUrlPath;
    protected static Url existingUrl;
    protected static UrlCheck existingUrlCheck;
    protected static MockWebServer mockServer;
    private static Transaction transaction;

    private static Path getTemplatesPath(String fileName) {
        return Paths.get("src", "test", "resources", "templates", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readTemplate(String fileName) throws IOException {
        Path filePath = getTemplatesPath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeEach
    final void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    final void afterEach() {
        transaction.rollback();
    }


    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrlPath = "http://127.0.0.1:" + port;

        existingUrl = new Url("https://yandex.ru");
        existingUrlCheck = new UrlCheck(200, "some title", "some h1", "some description");
        existingUrl.getUrlChecks().add(existingUrlCheck);
        existingUrl.save();

        mockServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setBody(readTemplate("index.html"));
        mockServer.enqueue(mockedResponse);
        mockServer.start();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        mockServer.shutdown();
    }
}
