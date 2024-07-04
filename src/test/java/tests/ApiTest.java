package tests;

import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static specs.TestSpec.*;

public class ApiTest extends TestBase {

    @Test
    @DisplayName("Проверка успешного запроса фамилии пользователя")
    void successfulGetLastNameUserTest() {
        step("Отправляем запрос на получении данных о фамилии пользователя", () -> {
            given(requestSpec)
                    .get("users/2")
                    .then()
                    .spec(statusCode200Spec)
                    .body("data.last_name", is("Weaver"));
        });
    }

    @Test
    @DisplayName("Проверка удаления пользователя")
    void deleteUserDataTest() {
        step("Отправляем запрос на удаление пользователя", () -> {
            given(requestSpec)
                    .when()
                    .delete("users/2")
                    .then()
                    .spec(statusCode204Spec);
        });
    }

    @Test
    @DisplayName("Проверка отсутствия данных пользователя")
    void checkMissingUserDataTest() {
        UpdateLoginResponseModel response = step("Отправляем запрос на проверку отсутствия пользователя",
                () -> given(requestSpec)
                        .when()
                        .get("users/23")
                        .then().log().all()
                        .statusCode(404)
                        .extract().as(UpdateLoginResponseModel.class));

        step("Проверяем отсутствие пользователя", () -> {
            assertThat(response.getName()).isNull();
            assertThat(response.getJob()).isNull();
            assertThat(response.getUpdatedAt()).isNull();
        });
    }

    @Test
    @DisplayName("Проверка успешного получения списка пользователей")
    void successfulGetUserDataTest() {
        given(requestSpec)
                .when()
                .queryParam("page", "1")
                .get("/users")
                .then()
                .spec(statusCode200Spec)
                .body("total", is(12));
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    void createUserTest() {
        LoginRequestModel userData = new LoginRequestModel();
        userData.setName("Jack");
        userData.setJob("fighter");
        CreateLoginResponseModel response = step("Делаем запрос на создание нового пользователя с именем и работой",
                () -> given(requestSpec)
                        .body(userData)
                        .when()
                        .post("/users")
                        .then()
                        .spec(statusCode201Spec)
                        .extract().as(CreateLoginResponseModel.class));
        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Jack");
            assertThat(response.getJob()).isEqualTo("fighter");
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCreatedAt()).isNotNull();
        });
    }


    @Test
    @DisplayName("Проверка редактирования пользователя")
    void updateUserTest() {
        LoginRequestModel userData = new LoginRequestModel();
        userData.setName("Jack");
        userData.setJob("QA_engineer");
        UpdateLoginResponseModel response = step("Отправляем запрос на редактирование пользователя", () ->
                given(requestSpec)
                        .body(userData)
                        .when()
                        .put("/users/2")
                        .then()
                        .spec(statusCode200Spec)
                        .extract().as(UpdateLoginResponseModel.class));

        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getName()).isEqualTo("Jack");
            assertThat(response.getJob()).isEqualTo("QA_engineer");
            assertThat(response.getUpdatedAt()).isNotNull();
        });

    }

    @Test
    @DisplayName("Проверка успешной регистрации пользователя")
    void successfulRegistrationTest() {
        AuthRequestModel authData = new AuthRequestModel();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("pistol");
        AuthResponseModel response = step("Отправляем запрос о проверке успещной регистрации пользователя", () ->
                given(requestSpec)
                        .body(authData)
                        .when()
                        .post("/register")
                        .then()
                        .spec(statusCode200Spec)
                        .extract().as(AuthResponseModel.class));

        step("Проверяем ответ на запрос", () -> {
            assertThat(response.getId()).isEqualTo(4);
            assertThat(response.getToken()).isNotNull();

        });


    }
}

