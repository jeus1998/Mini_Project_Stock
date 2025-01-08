package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private PessimisticLockStockService stockService;
    @Autowired
    private StockRepository stockRepository;
    @BeforeEach
    public void beforeEach(){
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }
    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }
    @Test
    public void 재고감소(){
        stockService.decrease(1L, 1L);
        // 100 - 1 = 99
        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(99, stock.getQuantity());
    }
    @Test
    public void 동시에_100개_요청() throws InterruptedException{
        int taskCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(taskCount);
        for (int t = 0; t < taskCount; t++) {
             executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                }
                finally {
                    latch.countDown();
                }
             });
        }
        latch.await();
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity());
    }
}