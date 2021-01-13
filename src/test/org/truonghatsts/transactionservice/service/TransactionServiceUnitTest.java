package org.truonghatsts.transactionservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.truonghatsts.transactionservice.domain.entity.Transaction;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;
import org.truonghatsts.transactionservice.domain.exception.TransactionException;
import org.truonghatsts.transactionservice.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.truonghatsts.transactionservice.constants.CodeMessage.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceUnitTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionService service;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;


    @Test
    void givenEmptyParentId_whenSaveTransaction_thenSaveSuccessfully() throws TransactionException {
        Transaction transaction = new Transaction();
        transaction.setAmount(100d);
        transaction.setType(TransactionType.DISBURSEMENT);

        service.save(transaction);

        verify(repository).save(transactionCaptor.capture());
        assertEquals(100d, transactionCaptor.getValue().getAmount(), 0.1d);
        assertEquals(TransactionType.DISBURSEMENT, transactionCaptor.getValue().getType());
    }

    @Test
    void givenWrongParentId_whenSaveTransaction_thenThrowException() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100d);
        transaction.setType(TransactionType.DISBURSEMENT);
        transaction.setParentId(123L);

        try {
            service.save(transaction);
        } catch (TransactionException e) {
            assertEquals(PARENT_TRANSACTION_NOT_FOUND_CODE, e.getCode());
            assertEquals(PARENT_TRANSACTION_NOT_FOUND_MESSAGE, e.getMessage());
        }
    }

    @Test
    void givenCorrectParentId_whenSaveTransaction_thenSaveTransaction() throws TransactionException {
        Transaction transaction = new Transaction();
        transaction.setAmount(100d);
        transaction.setType(TransactionType.DISBURSEMENT);
        transaction.setParentId(123L);

        when(repository.findById(123L)).thenReturn(Optional.of(new Transaction()));

        service.save(transaction);

        verify(repository).save(transactionCaptor.capture());
        assertEquals(100d, transactionCaptor.getValue().getAmount(), 0.1d);
        assertEquals(TransactionType.DISBURSEMENT, transactionCaptor.getValue().getType());
    }

    @Test
    void findById() throws TransactionException {

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction trans = service.findById(1L);
        assertEquals(1L, trans.getId());

        when(repository.findById(2L)).thenReturn(Optional.empty());
        try {
            service.findById(2L);
        } catch (Exception exception) {
            assertTrue(exception instanceof TransactionException);
            TransactionException te = (TransactionException) exception;
            assertEquals(TRANSACTION_NOT_FOUND_CODE, te.getCode());
            assertEquals(TRANSACTION_NOT_FOUND_MESSAGE, te.getMessage());
        }
    }

    @Test
    void findByType() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(TransactionType.REPAYMENT);

        when(repository.findByTypeOrderById(TransactionType.REPAYMENT)).thenReturn(Arrays.asList(transaction, transaction));
        when(repository.findByTypeOrderById(TransactionType.DISBURSEMENT)).thenReturn(new ArrayList<>());

        List<Transaction> repayments = service.findByType(TransactionType.REPAYMENT);
        List<Transaction> disbursements = service.findByType(TransactionType.DISBURSEMENT);

        assertEquals(0, disbursements.size());
        assertEquals(2, repayments.size());
    }

    @Test
    void sumTransaction() throws TransactionException {

        Transaction disbursement = new Transaction();
        disbursement.setId(1L);
        disbursement.setAmount(100d);
        disbursement.setType(TransactionType.DISBURSEMENT);

        Transaction firstRepayment = new Transaction();
        firstRepayment.setId(2L);
        firstRepayment.setAmount(-50d);
        firstRepayment.setType(TransactionType.REPAYMENT);

        Transaction secondRepayment = new Transaction();
        secondRepayment.setId(3L);
        secondRepayment.setAmount(-50d);
        secondRepayment.setType(TransactionType.REPAYMENT);

        when(repository.findById(1L)).thenReturn(Optional.of(disbursement));
        when(repository.findByParentIdOrderById(1L)).thenReturn(Arrays.asList(firstRepayment, secondRepayment));

        double sum = service.sumTransactions(1L);

        assertEquals(0d, sum);
    }


}