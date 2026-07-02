package com.kazakov.catalog.kafka.consumer;

import com.kazakov.catalog.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final StockService stockService;

    @KafkaListener(topics = "order-commands", groupId = "order-consumer-group")
    public void handleOrderCommand(ConsumerRecord<String, Object> record) throws ExecutionException, InterruptedException {
        String messageKey = record.key();
        KafkaOrderEvent event = (KafkaOrderEvent) record.value();

        switch (event.getEventType()) {
            case ORDER_STOCK_RESERVE -> {
                stockService.reserveStock(event.getEventId(), event.getOrderId() ,event.getItems());
                break;
            }
            case ORDER_STOCK_CANCEL -> {
                stockService.cancelReserved(event.getEventId(), event.getOrderId() ,event.getItems());
                break;
            }
            case ORDER_STOCK_CONFIRM -> {
                stockService.confirmReserved(event.getEventId(), event.getOrderId() ,event.getItems());
            }
        }
    }
}
