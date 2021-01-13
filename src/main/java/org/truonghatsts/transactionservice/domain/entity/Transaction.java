package org.truonghatsts.transactionservice.domain.entity;

import lombok.*;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;

import javax.persistence.*;

/**
 * @author Fenix truonghatsts@gmail.com
 */
@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "amount")
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;
    @Column(name = "parent_id")
    private Long parentId;
}
