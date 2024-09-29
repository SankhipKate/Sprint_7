package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.courier.Courier;
import praktikum.courier.CourierChecks;
import praktikum.courier.CourierClient;
import praktikum.courier.CourierCredentials;
import praktikum.order.Order;
import praktikum.order.OrderChecks;
import praktikum.order.OrderClient;

public class AcceptOrderTest {

    private CourierClient courierClient = new CourierClient();
    private OrderClient orderClient = new OrderClient();
    private CourierChecks courierCheck = new CourierChecks();
    private OrderChecks orderCheck = new OrderChecks();

    private int courierId;
    private int orderId;

    private Courier courier;

    // Создание курьера перед тестами
    @Before
    public void setUp() {
        courier = Courier.random();
        ValidatableResponse createCourierResponse = courierClient.createCourier(courier);
        courierCheck.checkCreated(createCourierResponse);

        ValidatableResponse loginResponse = courierClient.logIn(CourierCredentials.fromCourier(courier));
        courierId = courierCheck.checkLoggedIn(loginResponse);

        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                new String[]{"BLACK"}
        );
        ValidatableResponse createOrderResponse = orderClient.createOrder(order);
        orderId = orderCheck.checkOrderCreated(createOrderResponse);
    }

    // Удаление курьера после тестов
    @After
    public void tearDown() {
        if (courierId > 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Courier successfully accepts an order")
    public void courierAcceptsOrderSuccessfully() {
        ValidatableResponse acceptOrderResponse = orderClient.acceptOrder(orderId, courierId);
        orderCheck.checkOrderAccepted(acceptOrderResponse);
    }

    @Test
    @DisplayName("Accept order fails without courier ID")
    public void acceptOrderFailsWithoutCourierId() {
        ValidatableResponse response = orderClient.acceptOrderWithoutCourierId(orderId);
        orderCheck.checkBadRequestForAccept(response);
    }

    @Test
    @DisplayName("Accept order fails with non-existent courier ID")
    public void acceptOrderFailsWithNonExistentCourierId() {
        ValidatableResponse response = orderClient.acceptOrder(orderId, 999999);  // несуществующий ID курьера
        orderCheck.checkNotFoundCourier(response);
    }

    @Test
    @DisplayName("Accept order fails without order ID")
    public void acceptOrderFailsWithoutOrderId() {
        ValidatableResponse response = orderClient.acceptOrderWithoutOrderId(courierId);
        orderCheck.checkBadRequestForAccept(response);
    }

    @Test
    @DisplayName("Accept order fails with non-existent order ID")
    public void acceptOrderFailsWithNonExistentOrderId() {
        ValidatableResponse response = orderClient.acceptOrder(999999, courierId);  // несуществующий ID заказа
        orderCheck.checkNotFoundOrder(response);
    }
}
