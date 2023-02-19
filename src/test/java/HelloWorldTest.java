import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HelloWorldTest {
    @Test
    public void testRestAssured() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();

    }

    @Test
    public void testGetText() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testGetJsonHomework() {

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String message = response.get("messages.message[1]");
        System.out.println(message);
    }

    @Test
    public void testLongRedirect() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);

    }


    @Test
    public void testRedirectCycle() {
        int currentStatusCode = 0;
        String currentUrl = "https://playground.learnqa.ru/api/long_redirect";
        int cyclesCounter = 0;

        while (currentStatusCode != 200 || currentUrl != null) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(currentUrl)
                    .andReturn();

            currentUrl = response.getHeader("Location");
            currentStatusCode = response.getStatusCode();
            cyclesCounter++;
        }

        System.out.println("StatusCode: " + currentStatusCode);
        System.out.println("Cycles: " + cyclesCounter);
    }

    private static final String TASK_STATUS_NOT_READY = "Job is NOT ready";
    private static final String TASK_STATUS_READY = "Job is ready";

    @Test
    public void testCompleteTask() throws InterruptedException {
        JsonPath createTaskResponse = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = createTaskResponse.get("token");
        int seconds = createTaskResponse.get("seconds");


        JsonPath taskStatusResponse = getTaskStatusResponse(token);

        String status = taskStatusResponse.get("status");
        if (status.equals(TASK_STATUS_NOT_READY)) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
            JsonPath secondTaskStatusResponse = getTaskStatusResponse(token);
            status = secondTaskStatusResponse.get("status");
            String result = secondTaskStatusResponse.get("result");
            if (!status.equals(TASK_STATUS_READY) || result == null) {
                throw new IllegalStateException("Incorrect status after sleep");
            }
            System.out.println("Status:" + status);
            System.out.println("Result:" + result);
        } else {
            throw new IllegalStateException("Incorrect task status");
        }


    }

    private JsonPath getTaskStatusResponse(String token) {
        return RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

    }
}