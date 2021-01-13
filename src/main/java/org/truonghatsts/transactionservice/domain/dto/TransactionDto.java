package org.truonghatsts.transactionservice.domain.dto;

import lombok.Data;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;

@Data
public class TransactionDto {

    private Long id;
    private Double amount;
    private TransactionType type;
    private Long parentId;
}
