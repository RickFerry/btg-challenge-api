package tech.buildrun.btgpactual.orderms.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import tech.buildrun.btgpactual.orderms.exception.OrderProcessingException;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.service.OrderService;

import static tech.buildrun.btgpactual.orderms.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {
    private final OrderService orderService;

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listen(Message<OrderCreatedEvent> message) {
        try {
            OrderCreatedEvent event = message.getPayload();
            log.info("Mensagem recebida da fila: codigoPedido={}, codigoCliente={}",
                    event.codigoPedido(), event.codigoCliente());

            orderService.save(event);
            log.info("Pedido processado com sucesso: codigoPedido={}", event.codigoPedido());
        } catch (Exception e) {
            log.error("Erro ao processar mensagem da fila", e);
            throw new OrderProcessingException("Falha ao processar pedido da fila", e);
        }
    }
}
