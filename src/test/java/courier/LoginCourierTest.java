package courier;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.courier.Courier;
import praktikum.courier.CourierChecks;
import praktikum.courier.CourierClient;
import praktikum.courier.CourierCredentials;

public class LoginCourierTest {

    private CourierClient client = new CourierClient();
    private CourierChecks check = new CourierChecks();
    private Courier courier;
    int courierId;

    // Создание курьера перед тестами
    @Before
    public void setUp() {
        courier = Courier.random();
        ValidatableResponse createResponse = client.createCourier(courier);
        check.checkCreated(createResponse);
    }

    // Удаление курьера после тестов
    @After
    public void tearDown() {
        if (courierId > 0) {
            ValidatableResponse deleteResponse = client.delete(courierId);
            check.checkDeleted(deleteResponse);
        }
    }

    @Test
    @DisplayName("Courier can successfully log in and returns courier id")
    public void courierCanLogInAndReturnId() {
        ValidatableResponse loginResponse = client.logIn(CourierCredentials.fromCourier(courier));
        // Проверка успешного логина и получения ID
        courierId = check.checkLoggedIn(loginResponse);
    }

    @Test
    @DisplayName("Login fails with incorrect login or password")
    public void loginFailsWithIncorrectCredentials() {
        // Несуществующий логин
        CourierCredentials wrongLoginCredentials = new CourierCredentials("wrongLogin" + RandomStringUtils.randomAlphanumeric(5), courier.getPassword());
        ValidatableResponse wrongLoginResponse = client.logIn(wrongLoginCredentials);
        check.checkLoginError(wrongLoginResponse);

        // Некорректный пароль
        CourierCredentials wrongPasswordCredentials = new CourierCredentials(courier.getLogin(), "wrongPassword");
        ValidatableResponse wrongPasswordResponse = client.logIn(wrongPasswordCredentials);
        check.checkLoginError(wrongPasswordResponse);
    }

    @Test
    @DisplayName("Login fails with missing mandatory fields")
    public void loginFailsWithMissingMandatoryFields() {
        // Лог ин без пароля
        CourierCredentials credentialsWithoutPassword = new CourierCredentials(courier.getLogin(), null);
        ValidatableResponse responseWithoutPassword = client.logIn(credentialsWithoutPassword);
        check.checkLoginBadRequest(responseWithoutPassword);

        // Лог ин без логина
        CourierCredentials credentialsWithoutLogin = new CourierCredentials(null, courier.getPassword());
        ValidatableResponse responseWithoutLogin = client.logIn(credentialsWithoutLogin);
        check.checkLoginBadRequest(responseWithoutLogin);
    }
}
