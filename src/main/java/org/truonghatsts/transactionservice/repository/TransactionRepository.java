package org.truonghatsts.transactionservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.truonghatsts.transactionservice.domain.entity.Transaction;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findByTypeOrderById(TransactionType type);

    List<Transaction> findByParentIdOrderById(long parentId);
}
