package be.lpbconsult.befintax.wallet.mapper;

import be.lpbconsult.befintax.wallet.dto.WalletCreateDTO;
import be.lpbconsult.befintax.wallet.dto.WalletDTO;
import be.lpbconsult.befintax.wallet.entity.WalletEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AssetMapper.class })
public interface WalletMapper {

    WalletEntity toEntity(WalletCreateDTO dto);

    @Mapping(target = "assets", source = "assets")
    WalletDTO toDto(WalletEntity entity);
}
