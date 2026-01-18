package be.lpbconsult.befintax.wallet.mapper;

import be.lpbconsult.befintax.wallet.dto.AssetCreateDTO;
import be.lpbconsult.befintax.wallet.dto.AssetDTO;
import be.lpbconsult.befintax.wallet.entity.AssetEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AssetTransactionMapper.class })
public interface AssetMapper {

    AssetEntity toEntity(AssetCreateDTO dto);

    @Mapping(target = "transactions", source = "transactions")
    AssetDTO toDto(AssetEntity entity);
}

