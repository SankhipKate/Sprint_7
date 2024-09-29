package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.order.Order;
import praktikum.order.OrderClient;
import praktikum.order.OrderChecks;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private OrderClient orderClient = new OrderClient();
    private OrderChecks check = new OrderChecks();

    private String[] color;

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Test with color: {0}")
    public static Collection<Object[]> getColorData() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},     // Один цвет
                {new String[]{"GREY"}},      // Один цвет
                {new String[]{"BLACK", "GREY"}}, // Оба цвета
                {new String[]{}}             // Без цвета
        });
    }

    @Test
    @DisplayName("Create order with different color options")
    public void createOrderWithColor() {
        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                color
        );

        ValidatableResponse response = orderClient.createOrder(order);
        check.checkOrderCreated(response);  // Проверка успешного создания заказа
    }
}
