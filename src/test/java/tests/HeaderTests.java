package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeaderTests extends BaseTestCase {

    private static final String HEADER_NAME_X_SECRET_HOMEWORK = "x-secret-homework-header";
    private static final String HEADER_VALUE_X_SECRET_HOMEWORK = "Some secret value";


    @Test
    public void testHeader() {
        Response responseHeader = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String header = this.getHeader(responseHeader, HEADER_NAME_X_SECRET_HOMEWORK);
        assertEquals(
                HEADER_VALUE_X_SECRET_HOMEWORK,
                header,
                "Header value is not equal to " + HEADER_VALUE_X_SECRET_HOMEWORK
        );
    }

}
