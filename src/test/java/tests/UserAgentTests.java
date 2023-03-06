package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static lib.Assertions.assertJsonByName;

public class UserAgentTests {

    private static final String VALUE_USER_AGENT_1 = "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    private static final String VALUE_USER_AGENT_2 = "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1";
    private static final String VALUE_USER_AGENT_3 = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    private static final String VALUE_USER_AGENT_4 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0";
    private static final String VALUE_USER_AGENT_5 = "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
    private static final String PLATFORM_TAG = "platform";
    private static final String BROWSER_TAG = "browser";
    private static final String DEVICE_TAG = "device";

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testUserAgent(
            String userAgent,
            String platform,
            String browser,
            String device
    ) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        Response responseUserAgent = RestAssured
                .given()
                .headers(headers)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();

        assertJsonByName(responseUserAgent, PLATFORM_TAG, platform);
        assertJsonByName(responseUserAgent, BROWSER_TAG, browser);
        assertJsonByName(responseUserAgent, DEVICE_TAG, device);
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(VALUE_USER_AGENT_1, "Mobile", "No", "Android"),
                Arguments.of(VALUE_USER_AGENT_2, "Mobile", "Chrome", "iOS"),
                Arguments.of(VALUE_USER_AGENT_3, "Googlebot", "Unknown", "Unknown"),
                Arguments.of(VALUE_USER_AGENT_4, "Web", "Chrome", "No"),
                Arguments.of(VALUE_USER_AGENT_5, "Mobile", "No", "iPhone")
        );
    }
}
