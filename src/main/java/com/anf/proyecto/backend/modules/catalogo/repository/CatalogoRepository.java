package com.anf.proyecto.backend.modules.catalogo.repository;
import com.anf.proyecto.backend.modules.catalogo.entity.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Integer> {
    // Para encontrar el catálogo activo de una empresa
    List<Catalogo> findByEmpresa_EmpresaIdAndActivo(Integer empresaId, boolean activo);

    // CORREGIDO: Busca usando la ruta de propiedades camelCase de las entidades anidadas
    Optional<Catalogo> findByEmpresa_EmpresaIdAndCuenta_CuentaId(Integer empresaId, Integer cuentaId);
}