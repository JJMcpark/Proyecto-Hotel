package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNumDocumento(String numDocumento);
    boolean existsByNumDocumento(String numDocumento);
    boolean existsByNumDocumentoAndIdNot(String numDocumento, Long id);
    boolean existsByEmpresaId(Long empresaId);

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE Cliente c SET c.empresa = :nueva WHERE c.empresa.id = :oldId")
    int reasignarEmpresa(@org.springframework.data.repository.query.Param("oldId") Long oldId,
                         @org.springframework.data.repository.query.Param("nueva") com.proyecto.hotel.model.entities.Empresa nueva);
}
