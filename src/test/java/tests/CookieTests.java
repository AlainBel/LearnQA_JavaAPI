package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CookieTests extends BaseTestCase {

    private static final String COOKIE_VALUE = "hw_value";

    @Test
    public void testCookie() {
        Response responseCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String cookie = this.getCookie(responseCookie, "HomeWork");
        assertEquals(COOKIE_VALUE, cookie, "Cookie value is not equal to " + COOKIE_VALUE);
    }

}
