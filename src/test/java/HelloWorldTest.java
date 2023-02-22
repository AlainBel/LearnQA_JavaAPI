import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HelloWorldTest {
    @Test
    public void testFor200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();
        assertEquals(200, response.statusCode(), "Unexpected status code");

    }

    @Test
    public void testFor404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map2")
                .andReturn();
        assertEquals(404, response.statusCode(), "Unexpected status code");

    }

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

    private static final String AUTHORIZED = "You are authorized";
    private static final String LOGIN_VALUE = "super_admin";
    private static final String PASSWORD_TABLE_CAPTION = "Top 25 most common passwords by year according to SplashData";

    @Test
    public void testLogin() throws IOException {
        Set<String> passwords = getPasswords();
        boolean isPasswordFound = false;

        for (String password : passwords) {
            Response responseForLogin = responseForLogin(LOGIN_VALUE, password);
            String responseCookie = responseForLogin.getCookie("auth_cookie");

            Response responseForCheckCookie = responseForCheckCookie(responseCookie);
            String result = responseForCheckCookie.getBody().asString();

            if (result.equals(AUTHORIZED)) {
                isPasswordFound = true;
                System.out.println(AUTHORIZED);
                System.out.println("password: " + password);
                break;
            }
        }

        if (!isPasswordFound) {
            throw new IllegalStateException("Password was not found");
        }
    }

    private Set<String> getPasswords() throws IOException {
        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords").get();
        Optional<Element> passTable = doc.select(".wikitable").stream().filter(table -> {
            Element caption = table.select("caption").first();
            return caption != null && caption.text().equals(PASSWORD_TABLE_CAPTION);
        }).findFirst();
        if (passTable.isPresent()) {
            Elements tds = passTable.get().getElementsByTag("td");
            return tds.stream().map(Element::text).filter(val -> val.length() > 2).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    private Response responseForLogin(String login, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("login", login);
        data.put("password", password); //тут нужен список для перебора

        return RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                .andReturn();
    }

    private Response responseForCheckCookie(String responseCookie) {
        return RestAssured
                .given()
                .cookies("auth_cookie", responseCookie)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                .andReturn();
    }

}
