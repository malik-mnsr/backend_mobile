package com.example.mobileproject.repository;

import com.example.mobileproject.dto.DrugLiteDTO;
import com.example.mobileproject.entity.DrugReference;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DrugReferenceRepository
        extends JpaRepository<DrugReference, Long> {

    /* ---- recherche plein-texte sur name OU molecule ---- */
    @Query("""
        select d
        from  DrugReference d
        where lower(d.name)     like lower(concat('%', :q, '%'))
           or lower(d.molecule) like lower(concat('%', :q, '%'))
    """)
    Page<DrugReference> search(@Param("q") String q, Pageable pageable);

    /* ---- auto-complétion (projection) ---- */
    @Query("""
        select new com.example.mobileproject.dto.DrugLiteDTO(d.id, d.name)
        from   DrugReference d
        where  lower(d.name) like lower(concat('%', :term, '%'))
        order  by d.name
    """)
    List<DrugLiteDTO> autocomplete(@Param("term") String term, Pageable pageable);
}
