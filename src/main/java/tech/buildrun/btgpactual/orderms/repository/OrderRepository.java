package tech.buildrun.btgpactual.orderms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tech.buildrun.btgpactual.orderms.entity.OrderEntity;

import java.util.List;

public interface OrderRepository extends MongoRepository<OrderEntity, Long> {

    Page<OrderEntity> findAllByCustomerId(Long customerId, PageRequest pageRequest);

    List<OrderEntity> findAllByCustomerId(Long customerId);

    @Query("{ 'customerId': ?0 }")
    List<OrderEntity> findOrdersByCustomer(Long customerId);

    long countByCustomerId(Long customerId);
}
