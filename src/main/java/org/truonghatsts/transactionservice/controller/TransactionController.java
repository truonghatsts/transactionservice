package org.truonghatsts.transactionservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.truonghatsts.transactionservice.config.ModelMapper;
import org.truonghatsts.transactionservice.domain.dto.BaseDto;
import org.truonghatsts.transactionservice.domain.dto.TransactionDto;
import org.truonghatsts.transactionservice.domain.entity.Transaction;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;
import org.truonghatsts.transactionservice.domain.exception.TransactionException;
import org.truonghatsts.transactionservice.domain.payload.TransactionPayload;
import org.truonghatsts.transactionservice.service.TransactionService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/transaction")
@Validated
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto> saveTransaction(@RequestBody @Valid TransactionPayload payload, @PathVariable("id") Long id) throws TransactionException {

        Transaction transaction = ModelMapper.INSTANCE.payloadToEntity(payload);
        transaction.setId(id);
        service.save(transaction);
        return ResponseEntity.ok(new BaseDto());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto<TransactionDto>> getTransaction(@PathVariable("id") Long id) throws TransactionException {

        Transaction transaction = service.findById(id);
        TransactionDto transactionDto = ModelMapper.INSTANCE.entityToDto(transaction);
        return ResponseEntity.ok(new BaseDto<>(transactionDto));
    }

    @GetMapping(path = "/types/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto<List<TransactionDto>>> getTransaction(@PathVariable("type") TransactionType type) {

        List<Transaction> transactions = service.findByType(type);
        List<TransactionDto> transactionDtos = ModelMapper.INSTANCE.entitiesToDtos(transactions);
        return ResponseEntity.ok(new BaseDto<>(transactionDtos));
    }

    @GetMapping(path = "/sum/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseDto<Map<String, Double>>> sum(@PathVariable("id") Long id) throws TransactionException {

        double sum = service.sumTransactions(id);
        Map<String, Double> results = new HashMap<>();
        results.put("sum", sum);
        return ResponseEntity.ok(new BaseDto<>(results));
    }
}
