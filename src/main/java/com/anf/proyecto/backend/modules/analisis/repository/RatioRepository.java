package com.anf.proyecto.backend.modules.analisis.repository;

import com.anf.proyecto.backend.modules.analisis.entity.Ratio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatioRepository extends JpaRepository<Ratio, Integer> {

    /**
     * Busca todos los Ratios cuyo nombre de empresa asociado coincida
     * con el valor proporcionado, ignorando mayúsculas y minúsculas.
     *
     * Spring Data JPA infiere la consulta SQL a partir del nombre del método:
     * "findBy" - Prefijo de búsqueda.
     * "Empresa" - Campo 'empresa' en la entidad Ratio.
     * "_" - Separador para navegar a una propiedad del objeto relacionado.
     * "NombreEmpresa" - Campo 'nombreEmpresa' en la entidad Empresa.
     * "IgnoreCase" - Modificador para que la comparación no sea sensible a mayúsculas/minúsculas.
     *
     * @param nombreEmpresa El nombre de la empresa por el cual filtrar.
     * @return Una lista de entidades Ratio que cumplen con el criterio.
     */
    List<Ratio> findByEmpresa_NombreEmpresaIgnoreCase(String nombreEmpresa);

}