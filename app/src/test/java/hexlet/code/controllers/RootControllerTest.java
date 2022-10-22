package hexlet.code.controllers;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RootControllerTest extends ControllerTest {

    @Test
    void testIndex() {
        HttpResponse<String> response = Unirest.get(baseUrlPath).asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
