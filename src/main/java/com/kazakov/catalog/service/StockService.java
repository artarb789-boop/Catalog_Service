package com.kazakov.catalog.service;

import com.kazakov.catalog.model.entity.ProcessedEvent;
import com.kazakov.catalog.kafka.consumer.OrderEventType;
import com.kazakov.catalog.kafka.producer.CatalogReply;
import com.kazakov.catalog.kafka.producer.KafkaProducer;
import com.kazakov.catalog.mapper.StockMapper;
import com.kazakov.catalog.model.dto.OrderItemDto;
import com.kazakov.catalog.model.dto.stock.StockCreateDto;
import com.kazakov.catalog.model.dto.stock.StockResponseDto;
import com.kazakov.catalog.model.dto.stock.StockUpdateDto;
import com.kazakov.catalog.model.entity.Stock;
import com.kazakov.catalog.repository.ProcessedEventRepository;
import com.kazakov.catalog.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
@Service
public class StockService {

    private final StockRepository repository;
    private final StockMapper mapper;
    private final KafkaProducer producer;
    private final ProcessedEventRepository  processedEventRepository;

    public StockService(StockRepository repository, StockMapper mapper, KafkaProducer producer, ProcessedEventRepository processedEventRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.producer = producer;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public StockResponseDto createStock(StockCreateDto dto) {
        Stock stock = mapper.toEntity(dto);
        repository.save(stock);
        return mapper.toDto(stock);
    }

    public StockResponseDto findById(UUID uuid) {
        Optional<Stock> stock = repository.findById(uuid);
        if(stock.isEmpty()) {
            throw new EntityNotFoundException("Stock not found with uuid: " + uuid);
        }
        return mapper.toDto(stock.get());
    }

    public List<StockResponseDto> findAll(Pageable pageable) {
        Page<Stock> page = repository.findAll(pageable);
        return page.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public StockResponseDto findByProductId(UUID uuid) {
        Optional<Stock> stock = repository.findByProductId(uuid);
        if(stock.isEmpty()) {
            throw new EntityNotFoundException("Stock not found with uuid: " + uuid);
        }
        return mapper.toDto(stock.get());
    }

    @Transactional
    public StockResponseDto findByProductSku(String sku) {
        Optional<Stock> stock = repository.findByProductSku(sku);
        if(stock.isEmpty()) {
            throw new EntityNotFoundException("Stock not found with product: " + sku);
        }
        return mapper.toDto(stock.get());
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public StockResponseDto updateStock(UUID id, StockUpdateDto dto) {
        Optional<Stock> stock = repository.findById(id);
        if(stock.isEmpty()) {
            throw new EntityNotFoundException("Stock not found with id: " + id);
        }
        Stock stockToUpdate = stock.get();
        stockToUpdate.setQuantity(dto.getQuantity());
        stockToUpdate.setReserved(dto.getReserved());
        repository.save(stockToUpdate);

        return mapper.toDto(stockToUpdate);
    }

    @Transactional
    public void reserveStock(UUID eventId, UUID orderId, List<OrderItemDto> items) throws ExecutionException, InterruptedException {
        if(processedEventRepository.existsById(eventId)) {
            log.info("Event with id {} is already processed", eventId);
            return;
        }
        try {
        for (OrderItemDto item : items) {
            Stock stockToReserve = repository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product is not found: " + item.getProductId()));
            if (!checkAvailability(stockToReserve, item.getQuantity())) {
                throw new RuntimeException("Stock not available for product " + item.getProductId());
            }
            stockToReserve.setReserved(stockToReserve.getReserved() + item.getQuantity());
            repository.save(stockToReserve);
        }

            processedEventRepository.save(new ProcessedEvent(eventId, Instant.now()));

            CatalogReply event = CatalogReply.builder()
                    .eventId(UUID.randomUUID())
                    .orderId(orderId)
                    .eventType(OrderEventType.RESERVE_SUCCESS)
                    .timestamp(Instant.now())
                    .build();
        producer.send("catalog-replies", event.getOrderId().toString(), event);
    } catch (Exception e) {
            log.warn("Failed to reserve stock for order {}: {}", orderId, e.getMessage());
            processedEventRepository.save(new ProcessedEvent(eventId, Instant.now()));
        CatalogReply reply = CatalogReply.builder()
                .eventId(UUID.randomUUID())
                .orderId(orderId)
                .eventType(OrderEventType.RESERVE_FAILED)
                .timestamp(Instant.now())
                .build();
        producer.send("catalog-replies", reply.getOrderId().toString(), reply);
    }
    }

    @Transactional
    public void cancelReserved(UUID eventId, UUID orderId, List<OrderItemDto> items) {
        if (processedEventRepository.existsById(eventId)) {
            log.info("Cancel event {} already processed", eventId);
            return;
        }
        for (OrderItemDto item : items) {
            Stock stockToCancel = repository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            int reserved =  stockToCancel.getReserved() - item.getQuantity();;
            stockToCancel.setReserved(reserved);
            repository.save(stockToCancel);
        }
        processedEventRepository.save(new ProcessedEvent(eventId, Instant.now()));
    }

    @Transactional
    public void confirmReserved(UUID eventId, UUID orderId, List<OrderItemDto> items) {
        if (processedEventRepository.existsById(eventId)) {
            log.info("Confirm event {} already processed", eventId);
            return;
        }
        for (OrderItemDto item : items) {
            Stock stockToConfirm = repository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product is not found: " + item.getProductId()));
            stockToConfirm.setQuantity(stockToConfirm.getQuantity() - item.getQuantity());
            stockToConfirm.setReserved(stockToConfirm.getReserved() - item.getQuantity());
            repository.save(stockToConfirm);
        }
        processedEventRepository.save(new ProcessedEvent(eventId, Instant.now()));
    }

    protected boolean checkAvailability(Stock stock, int quantity) {
        int available = stock.getQuantity() - stock.getReserved();
        return available >= quantity;
    }

}
