package tech.buildrun.btgpactual.orderms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import tech.buildrun.btgpactual.orderms.controller.dto.OrderResponse;
import tech.buildrun.btgpactual.orderms.entity.OrderEntity;
import tech.buildrun.btgpactual.orderms.entity.OrderItem;
import tech.buildrun.btgpactual.orderms.exception.OrderProcessingException;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    public static final String TOTAL = "total";
    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public void save(OrderCreatedEvent event) {
        try {
            log.info("Salvando novo pedido: codigoPedido={}, codigoCliente={}", event.codigoPedido(), event.codigoCliente());

            var entity = new OrderEntity();
            entity.setOrderId(event.codigoPedido());
            entity.setCustomerId(event.codigoCliente());
            entity.setItems(getOrderItems(event));
            entity.setTotal(getTotal(event));

            OrderEntity saved = orderRepository.save(entity);
            log.info("Pedido salvo com sucesso: orderId={}, customerId={}, total={}",
                    saved.getOrderId(), saved.getCustomerId(), saved.getTotal());
        } catch (Exception e) {
            log.error("Erro ao salvar pedido: codigoPedido={}, codigoCliente={}",
                    event.codigoPedido(), event.codigoCliente(), e);
            throw new OrderProcessingException("Falha ao processar pedido: " + e.getMessage(), e);
        }
    }

    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
        log.debug("Buscando pedidos para cliente: customerId={}, page={}, pageSize={}",
                customerId, pageRequest.getPageNumber(), pageRequest.getPageSize());

        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);
        log.info("Encontrados {} pedidos para cliente: {}", orders.getTotalElements(), customerId);

        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        try {
            log.debug("Calculando total de pedidos para cliente: customerId={}", customerId);

            var aggregations = newAggregation(
                    match(Criteria.where("customerId").is(customerId)),
                    group().sum(TOTAL).as(TOTAL)
            );

            var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);
            var result = response.getUniqueMappedResult();

            BigDecimal total = result != null ? new BigDecimal(result.get(TOTAL).toString()) : BigDecimal.ZERO;
            log.info("Total calculado para cliente {}: {}", customerId, total);

            return total;
        } catch (Exception e) {
            log.error("Erro ao calcular total de pedidos para cliente: {}", customerId, e);
            return BigDecimal.ZERO;
        }
    }

    public long countOrdersByCustomerId(Long customerId) {
        log.debug("Contando pedidos para cliente: customerId={}", customerId);
        return orderRepository.countByCustomerId(customerId);
    }

    public List<OrderEntity> listOrdersByCustomerId(Long customerId) {
        log.debug("Listando todos os pedidos para cliente: customerId={}", customerId);
        return orderRepository.findAllByCustomerId(customerId);
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
        return event.itens().stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList();
    }
}
