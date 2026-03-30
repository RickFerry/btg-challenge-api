package tech.buildrun.btgpactual.orderms.exception;

/**
 * Exceção customizada para erros de processamento de pedidos
 */
public class OrderProcessingException extends RuntimeException {

    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
