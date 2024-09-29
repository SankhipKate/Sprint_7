package praktikum.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class OrderChecks {

    @Step("Check order creation successful")
    public int checkOrderCreated(ValidatableResponse response) {
        response
                .assertThat()
                // Проверка успешного создания заказа
                .statusCode(HTTP_CREATED)
                .and()
                // Проверка, что поле "track" не null
                .body("track", notNullValue());
        // Возвращаем идентификатор заказа (track)
        return response.extract().path("track");
    }

    @Step("Check orders list is returned successfully")
    public void checkOrdersListReturned(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_OK)  // Проверка, что запрос успешен
                .and()
                .body("orders", not(empty()));  // Проверка, что список заказов не пуст
    }

    @Step("Check order accepted successfully")
    public void checkOrderAccepted(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_OK)  // Успешный ответ 200
                .body("ok", equalTo(true));  // Проверка, что поле "ok" true
    }

    @Step("Check bad request for accept order (400)")
    public void checkBadRequestForAccept(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)  // Ожидаем код 400
                .body("message", equalTo("Недостаточно данных для поиска"));  // Проверка текста ошибки
    }

    @Step("Check courier not found (404)")
    public void checkNotFoundCourier(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_NOT_FOUND)  // Ожидаем код 404
                .body("message", equalTo("Курьера с таким id не существует"));  // Проверка текста ошибки
    }

    @Step("Check order not found (404)")
    public void checkNotFoundOrder(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_NOT_FOUND)  // Ожидаем код 404
                .body("message", equalTo("Заказа с таким id не существует"));  // Проверка текста ошибки
    }

    @Step("Check order is returned successfully")
    public void checkOrderReturned(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_OK)  // Успешный ответ 200
                .body("order", notNullValue());  // Проверка, что объект заказа не null
    }

    @Step("Check order not found for incorrect track number")
    public void checkOrderNotFoundForIncorrectTrack(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HTTP_NOT_FOUND)  // Ожидаем код 404
                .body("message", equalTo("Заказ не найден"));  // Проверка текста ошибки
    }

}
