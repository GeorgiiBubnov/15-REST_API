import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ApiTest extends TestBase {

    @Test
    @DisplayName("Проверка успешного запроса фамилии пользователя")
    void successfulGetLastNameUserTest() {
        given()
                .log().all()
                .get("users/2")
                .then().log().all()
                .statusCode(200)
                .body("data.last_name", is("Weaver"));
    }

    @Test
    @DisplayName("Проверка удаления пользователя")
    void deleteUserDataTest() {
        given()
                .log().all()
                .when()
                .delete("users/2")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("Проверка отсутствия данных пользователя")
    void checkMissingUserDataTest() {
        given()
                .log().all()
                .when()
                .get("users/23")
                .then().log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка успешного получения списка пользователей")
    void successfulGetUserDataTest() {
        given()
                .log().all()
                .when()
                .queryParam("page", "1")
                .get("/users")
                .then().log().all()
                .statusCode(200)
                .body("total", is(12));
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    void createUserTest() {
        given()
                .body("{\n" +
                        "    \"name\": \"Jack\",\n" +
                        "    \"job\": \"fighter\"\n" +
                        "}")
                .contentType(JSON)
                .log().all()
                .when()
                .post("/users")
                .then().log().all()
                .statusCode(201)
                .body("name", is("Jack"))
                .body("job", is("fighter"))
                .body("id", is(notNullValue()))
                .body("createdAt", is(notNullValue()));

    }

    @Test
    @DisplayName("Проверка редактирования пользователя")
    void updateUserTest() {
        given()
                .body("{\n" +
                        "    \"name\": \"Jack\",\n" +
                        "    \"job\": \"QA_engineer\"\n" +
                        "}")
                .contentType(JSON)
                .log().all()
                .when()
                .put("/users/2")
                .then().log().all()
                .statusCode(200)
                .body("name", is("Jack"))
                .body("job", is("QA_engineer"))
                //.body("id", is(notNullValue()))
                .body("updatedAt", is(notNullValue()));
    }

    @Test
    @DisplayName("Проверка успешной регистрации пользователя")
    void successfulRegistrationTest() {
        String authData = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";
        given()
                .body(authData)
                .contentType(JSON)
                .log().all()
                .when()
                .post("/register")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(4))
                .body("token", notNullValue());

    }
}

