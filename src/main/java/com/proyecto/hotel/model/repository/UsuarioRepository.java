package com.proyecto.hotel.model.repository;

import com.proyecto.hotel.model.entities.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByNumDocumento(String numDocumento);

    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findUsuarioById(Long id);

    boolean existsByNumDocumento(String numDocumento);

    @EntityGraph(attributePaths = {"rol", "tipoDocumento"})
    List<Usuario> findByRolNombreOrderByNombreAsc(String rolNombre);

    @EntityGraph(attributePaths = {"rol", "tipoDocumento"})
    Optional<Usuario> findByIdAndRolNombre(Long id, String rolNombre);
}
