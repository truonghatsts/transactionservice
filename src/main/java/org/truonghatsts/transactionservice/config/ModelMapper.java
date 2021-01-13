package org.truonghatsts.transactionservice.config;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.truonghatsts.transactionservice.domain.dto.TransactionDto;
import org.truonghatsts.transactionservice.domain.entity.Transaction;
import org.truonghatsts.transactionservice.domain.payload.TransactionPayload;

import java.util.List;

/**
 * @author Fenix truonghatsts@gmail.com
 */
@Mapper
public interface ModelMapper {

    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    Transaction payloadToEntity(TransactionPayload payload);
    TransactionDto entityToDto(Transaction transaction);
    List<TransactionDto> entitiesToDtos(List<Transaction> transactions);

}
