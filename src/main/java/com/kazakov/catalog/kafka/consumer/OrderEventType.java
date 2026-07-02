package com.kazakov.catalog.kafka.consumer;

public enum OrderEventType {
    ORDER_STOCK_RESERVE,
    ORDER_STOCK_CANCEL,
    ORDER_STOCK_CONFIRM,
    RESERVE_SUCCESS,
    RESERVE_FAILED
}
