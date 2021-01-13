package org.truonghatsts.transactionservice.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.truonghatsts.transactionservice.domain.entity.Transaction;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("test")
class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository repository;

    @Test
    @Order(1)
    void addTransactions() {

        Transaction disburseTransaction = new Transaction();
        disburseTransaction.setId(1L);
        disburseTransaction.setAmount(100d);
        disburseTransaction.setType(TransactionType.DISBURSEMENT);
        disburseTransaction = repository.save(disburseTransaction);

        Transaction firstRepayTransaction = new Transaction();
        firstRepayTransaction.setId(2L);
        firstRepayTransaction.setParentId(1L);
        firstRepayTransaction.setAmount(-50d);
        firstRepayTransaction.setType(TransactionType.REPAYMENT);
        repository.save(firstRepayTransaction);

        Transaction secondRepayTransaction = new Transaction();
        secondRepayTransaction.setId(3L);
        secondRepayTransaction.setParentId(1L);
        secondRepayTransaction.setAmount(-50d);
        secondRepayTransaction.setType(TransactionType.REPAYMENT);
        repository.save(secondRepayTransaction);

        assertEquals(disburseTransaction, repository.findById(1L).get());
        assertEquals(firstRepayTransaction, repository.findById(2L).get());
        assertEquals(secondRepayTransaction, repository.findById(3L).get());
    }


    @Test
    @Order(2)
    void findByTypeOrderById() {
        assertEquals(1, repository.findByTypeOrderById(TransactionType.DISBURSEMENT).size());
        assertEquals(2, repository.findByTypeOrderById(TransactionType.REPAYMENT).size());
        assertEquals(0, repository.findByTypeOrderById(TransactionType.FEE_APPLIED).size());
    }

    @Test
    @Order(3)
    void findByParentIdOrderById() {
        List<Transaction> subTransactions = repository.findByParentIdOrderById(1L);
        assertEquals(2, subTransactions.size());
        for (Transaction transaction : subTransactions) {
            assertEquals(TransactionType.REPAYMENT, transaction.getType());
            assertEquals(-50d, transaction.getAmount());
        }
    }
}