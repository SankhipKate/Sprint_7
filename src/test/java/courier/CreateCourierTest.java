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

public class CreateCourierTest {
    private CourierClient client = new CourierClient();
    private CourierChecks check = new CourierChecks();
    private Courier courier;
    int courierId;

    //создание объекта
    @Before
    public void setUp() {
        courier = Courier.random();
    }

    @Test
    @DisplayName("Courier happy path")
    public void creatingCourier() {
        ValidatableResponse createResponse = client.createCourier(courier);
        // Проверка успешного создания
        check.checkCreated(createResponse);
        // Логинимся, чтобы получить ID курьера
        ValidatableResponse loginResponse = client.logIn(CourierCredentials.fromCourier(courier));
        courierId = check.checkLoggedIn(loginResponse);
    }

    @Test
    @DisplayName("Courier creation failure for duplicate")
    public void shouldNotAllowDuplicateCourierCreation() {
        // Создаем первого курьера
        ValidatableResponse firstResponse = client.createCourier(courier);
        // Проверка успешного создания
        check.checkCreated(firstResponse);
        // Логинимся, чтобы получить ID курьера
        ValidatableResponse loginResponse = client.logIn(CourierCredentials.fromCourier(courier));
        courierId = check.checkLoggedIn(loginResponse);

        ///Попытка создать курьера с таким же логином
        ValidatableResponse secondResponse = client.createCourier(courier);
        //проверка получения ошибки 409
        check.checkLoginConflict(secondResponse);
    }

    // Тесты для проверки отсутствия обязательных полей
    @Test
    @DisplayName("Courier creation failure when login is null")
    public void shouldFailWhenLoginIsNull() {
        Courier courierWithoutLogin = new Courier(null, "password", "firstName");
        ValidatableResponse response = client.createCourier(courierWithoutLogin);
        check.checkBadRequestForCreation(response);
    }

    @Test
    @DisplayName("Courier creation failure when password is null")
    public void shouldFailWhenPasswordIsNull() {
        Courier courierWithoutPassword = new Courier(RandomStringUtils.randomAlphanumeric(5,15), null, "firstName");
        ValidatableResponse response = client.createCourier(courierWithoutPassword);
        check.checkBadRequestForCreation(response);
    }

    @Test
    @DisplayName("Courier creation failure when firstName is null")
    public void shouldFailWhenFirstNameIsNull() {
        Courier courierWithoutFirstName = new Courier(RandomStringUtils.randomAlphanumeric(5,15), "password", null);
        ValidatableResponse response = client.createCourier(courierWithoutFirstName);
        check.checkBadRequestForCreation(response);
    }

    @Test
    @DisplayName("Courier creation failure when login is empty")
    public void shouldFailWhenLoginIsEmpty() {
        Courier courierWithEmptyLogin = new Courier("", "password", "firstName");
        ValidatableResponse response = client.createCourier(courierWithEmptyLogin);
        check.checkBadRequestForCreation(response);
    }

    @Test
    @DisplayName("Courier creation failure when password is empty")
    public void shouldFailWhenPasswordIsEmpty() {
        Courier courierWithEmptyPassword = new Courier(RandomStringUtils.randomAlphanumeric(5,15), "", "firstName");
        ValidatableResponse response = client.createCourier(courierWithEmptyPassword);
        check.checkBadRequestForCreation(response);
    }

    @Test
    @DisplayName("Courier creation failure when firstName is empty")
    public void shouldFailWhenFirstNameIsEmpty() {
        Courier courierWithEmptyFirstName = new Courier(RandomStringUtils.randomAlphanumeric(5,15), "password", "");
        ValidatableResponse response = client.createCourier(courierWithEmptyFirstName);
        check.checkBadRequestForCreation(response);
    }

    @After
    public void tearDown() {
        // Если ID курьера был получен (курьер был создан), выполняем удаление
        if (courierId > 0) {
            try {
                ValidatableResponse deleteResponse = client.delete(courierId);
                check.checkDeleted(deleteResponse);
            } catch (Exception e) {
                System.out.println("Ошибка при удалении курьера: " + e.getMessage());
            }
        }
    }
}
