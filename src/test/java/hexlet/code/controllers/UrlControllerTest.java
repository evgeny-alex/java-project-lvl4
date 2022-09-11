package hexlet.code.controllers;


import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UrlControllerTest extends ControllerTest {

    @Test
    void testIndex() {
        HttpResponse<String> response = Unirest
                .get(baseUrlPath + "/urls")
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testShow() {
        HttpResponse<String> response = Unirest
                .get(baseUrlPath + "/urls/" + existingUrl.getId())
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testStore1() {
        String inputUrl = "https://mail.ru";
        HttpResponse responsePost = Unirest
                .post(baseUrlPath + "/urls")
                .field("url", inputUrl)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse<String> response = Unirest
                .get(baseUrlPath + "/urls")
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);

        Url actualUrl = new QUrl()
                .name.equalTo(inputUrl)
                .findOne();

        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(inputUrl);
    }


    @Test
    void testStore2() {
        String url = mockServer.url("/").toString().replaceAll("/$", "");

        HttpResponse<String> responseAddUrl = Unirest
                .post(baseUrlPath + "/urls")
                .field("url", url)
                .asEmpty();

        Url actualUrl = new QUrl()
                .name.equalTo(url)
                .findOne();

        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo(url);

        HttpResponse<String> responseCheck = Unirest
                .post(baseUrlPath + "/urls/" + actualUrl.getId() + "/checks")
                .asEmpty();

        HttpResponse<String> response = Unirest
                .get(baseUrlPath + "/urls/" + actualUrl.getId())
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);

        UrlCheck actualCheckUrl = new QUrlCheck()
                .url.equalTo(actualUrl)
                .orderBy()
                .createdAt.desc()
                .findOne();

        assertThat(actualCheckUrl).isNotNull();
        assertThat(actualCheckUrl.getStatusCode()).isEqualTo(200);
        assertThat(actualCheckUrl.getTitle()).isEqualTo("Title");
        assertThat(actualCheckUrl.getH1()).isEqualTo("Hello world! Test by page analyzer.");
        assertThat(actualCheckUrl.getDescription()).isEqualTo("description of test page");

    }
}
