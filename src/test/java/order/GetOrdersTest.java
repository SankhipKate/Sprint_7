package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import praktikum.order.OrderClient;
import praktikum.order.OrderChecks;

public class GetOrdersTest {

    private OrderClient orderClient = new OrderClient();
    private OrderChecks check = new OrderChecks();

    @Test
    @DisplayName("Get orders list successfully")
    public void getOrdersListSuccessfully() {
        // Отправляем запрос на получение всех заказов
        ValidatableResponse response = orderClient.getOrders();

        // Проверяем, что список заказов возвращен успешно
        check.checkOrdersListReturned(response);
    }
}
