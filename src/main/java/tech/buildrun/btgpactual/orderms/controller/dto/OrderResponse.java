package tech.buildrun.btgpactual.orderms.controller.dto;

import tech.buildrun.btgpactual.orderms.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(Long orderId, Long customerId, BigDecimal total, List<OrderItemResponse> items) {

    public static OrderResponse fromEntity(OrderEntity entity) {
        List<OrderItemResponse> items = entity.getItems() != null
                ? entity.getItems().stream()
                  .map(item -> new OrderItemResponse(item.getProduct(), item.getQuantity(), item.getPrice()))
                  .toList()
                : List.of();

        return new OrderResponse(entity.getOrderId(), entity.getCustomerId(), entity.getTotal(), items);
    }

    public record OrderItemResponse(String product, Integer quantity, BigDecimal price) {
    }
}
