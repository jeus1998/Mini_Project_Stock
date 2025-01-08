package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private final StockRepository stockRepository;
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
    @Transactional
    public void decrease(Long id, Long quantity){
        // Stock 조회
        // 재고 감소
        // 갱신된 값을 저장

        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}
