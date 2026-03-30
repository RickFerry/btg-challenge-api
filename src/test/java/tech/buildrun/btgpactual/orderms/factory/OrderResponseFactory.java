package tech.buildrun.btgpactual.orderms.factory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import tech.buildrun.btgpactual.orderms.controller.dto.OrderResponse;
import tech.buildrun.btgpactual.orderms.controller.dto.OrderResponse.OrderItemResponse;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponseFactory {

    public static Page<OrderResponse> buildWithOneItem() {
        var items = List.of(
            new OrderItemResponse("lápis", 100, BigDecimal.valueOf(1.10)),
            new OrderItemResponse("caderno", 10, BigDecimal.valueOf(1.00))
        );
        var orderResponse = new OrderResponse(1L, 2L, BigDecimal.valueOf(20.50), items);

        return new PageImpl<>(List.of(orderResponse));
    }
}
