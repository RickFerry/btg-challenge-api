package tech.buildrun.btgpactual.orderms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.buildrun.btgpactual.orderms.controller.dto.ApiResponse;
import tech.buildrun.btgpactual.orderms.controller.dto.OrderResponse;
import tech.buildrun.btgpactual.orderms.controller.dto.PaginationResponse;
import tech.buildrun.btgpactual.orderms.service.OrderService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * Listar pedidos de um cliente com paginação
     */
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable Long customerId,
                                                                 @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        log.info("GET /customers/{}/orders?page={}&pageSize={}", customerId, page, pageSize);

        var pageResponse = orderService.findAllByCustomerId(customerId, PageRequest.of(page, pageSize));
        var totalOnOrders = orderService.findTotalOnOrdersByCustomerId(customerId);
        var orderCount = orderService.countOrdersByCustomerId(customerId);

        return ResponseEntity.ok(new ApiResponse<>(
                Map.of(
                        "totalOnOrders", totalOnOrders,
                        "quantityOrders", orderCount
                ),
                pageResponse.getContent(),
                PaginationResponse.fromPage(pageResponse)
        ));
    }

    /**
     * Obter informações resumidas de um cliente
     */
    @GetMapping("/customers/{customerId}/summary")
    public ResponseEntity<Map<String, Object>> getCustomerSummary(@PathVariable Long customerId) {
        log.info("GET /customers/{}/summary", customerId);

        long totalOrders = orderService.countOrdersByCustomerId(customerId);
        var totalValue = orderService.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok(Map.of(
                "customerId", customerId,
                "totalOrders", totalOrders,
                "totalValue", totalValue
        ));
    }

    /**
     * Listar todos os pedidos de um cliente (sem paginação)
     */
    @GetMapping("/customers/{customerId}/all-orders")
    public ResponseEntity<Map<String, Object>> getAllOrders(@PathVariable Long customerId) {
        log.info("GET /customers/{}/all-orders", customerId);

        var orders = orderService.listOrdersByCustomerId(customerId);
        var totalValue = orderService.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok(Map.of(
                "customerId", customerId,
                "orders", orders,
                "totalValue", totalValue,
                "quantity", orders.size()
        ));
    }
}
