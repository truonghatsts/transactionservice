package org.truonghatsts.transactionservice.domain.payload;

import lombok.Data;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;

import javax.validation.constraints.NotNull;

@Data
public class TransactionPayload {

    @NotNull
    private Double amount;
    @NotNull
    private TransactionType type;
    private Long parentId;
}
