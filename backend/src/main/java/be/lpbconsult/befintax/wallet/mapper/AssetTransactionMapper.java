package be.lpbconsult.befintax.wallet.mapper;

import be.lpbconsult.befintax.wallet.dto.AssetTransactionCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetTransactionDTO;
import be.lpbconsult.befintax.wallet.entity.AssetTransactionEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetTransactionMapper {

    AssetTransactionEntity toEntity(AssetTransactionCreateDTO dto);

    AssetTransactionDTO toDto(AssetTransactionEntity entity);
}

