package be.lpbconsult.befintax.market.repository;

import be.lpbconsult.befintax.market.model.InstrumentEntity;
import be.lpbconsult.befintax.market.model.InstrumentType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;


@Repository
public interface InstrumentRepository extends JpaRepository<InstrumentEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM InstrumentEntity i WHERE i.category = :category")
    void deleteByCategory(@Param("category") InstrumentType category);

    @Query("""
        SELECT i FROM InstrumentEntity i 
        WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(i.symbol) LIKE LOWER(CONCAT('%', :query, '%')))
           AND i.currency = 'EUR'
        ORDER BY i.name ASC
    """)
    List<InstrumentEntity> searchInstruments(@Param("query") String query, Pageable pageable);
}