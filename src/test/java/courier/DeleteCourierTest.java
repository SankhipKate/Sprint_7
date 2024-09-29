package courier;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.courier.Courier;
import praktikum.courier.CourierChecks;
import praktikum.courier.CourierClient;
import praktikum.courier.CourierCredentials;

public class DeleteCourierTest {

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

        // Логинимся, чтобы получить ID курьера
        ValidatableResponse loginResponse = client.logIn(CourierCredentials.fromCourier(courier));
        courierId = check.checkLoggedIn(loginResponse);
    }

    @After
    public void tearDown() {
        // Удаляем курьера только если он существует
        if (courierId > 0) {
            ValidatableResponse deleteResponse = client.delete(courierId);
            check.checkDeleted(deleteResponse);
        }
    }

    @Test
    @DisplayName("Successful courier deletion")
    public void successfulCourierDeletion() {
        // Удаляем курьера
        ValidatableResponse deleteResponse = client.delete(courierId);

        // Проверяем, что удаление успешное и возвращается ok: true
        check.checkDeleted(deleteResponse);
        // Сбрасываем courierId, чтобы метод @After не пытался его снова удалить
        courierId = 0;
    }

    @Test
    @DisplayName("Deleting courier without ID fails")
    public void deleteCourierWithoutIdFails() {
        // Пробуем удалить курьера без указания ID
        ValidatableResponse deleteResponse = client.deleteWithoutId();

        // Проверяем, что запрос без ID возвращает ошибку 400
        check.checkBadRequestForDeletion(deleteResponse);
    }

    @Test
    @DisplayName("Deleting non-existent courier fails")
    public void deleteNonExistentCourierFails() {
        // Пробуем удалить курьера с несуществующим ID
        ValidatableResponse deleteResponse = client.delete(999999); // ID, которого нет в системе

        // Проверяем, что запрос с несуществующим ID возвращает ошибку 404
        check.checkNotFoundForDeletion(deleteResponse);
    }
}
