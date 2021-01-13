package org.truonghatsts.transactionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.truonghatsts.transactionservice.domain.entity.Transaction;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;
import org.truonghatsts.transactionservice.domain.exception.TransactionException;
import org.truonghatsts.transactionservice.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

import static org.truonghatsts.transactionservice.constants.CodeMessage.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class TransactionService {

    public final TransactionRepository repository;

    public Transaction save(Transaction transaction) throws TransactionException {
        if(transaction.getParentId() != null) {
            Optional<Transaction> parent = repository.findById(transaction.getParentId());
            if(!parent.isPresent()) {
                throw new TransactionException(PARENT_TRANSACTION_NOT_FOUND_CODE, PARENT_TRANSACTION_NOT_FOUND_MESSAGE);
            }
        }
        return repository.save(transaction);
    }

    public Transaction findById(Long id) throws TransactionException {
        Optional<Transaction> transaction = repository.findById(id);
        if(transaction.isPresent()) {
            return transaction.get();
        }
        throw new TransactionException(TRANSACTION_NOT_FOUND_CODE, TRANSACTION_NOT_FOUND_MESSAGE);
    }

    public List<Transaction> findByType(TransactionType type) {
        return repository.findByTypeOrderById(type);
    }

    public double sumTransactions(Long parentId) throws TransactionException {
        Transaction parentTransaction = findById(parentId);
        double subtotal = repository.findByParentIdOrderById(parentId).stream().mapToDouble(Transaction::getAmount).sum();
        return parentTransaction.getAmount() + subtotal;
    }
}
