package labs.pinheiro.springbootconsole.arearestrita.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import labs.pinheiro.springbootconsole.arearestrita.entity.CotaEvento;

@Repository
public interface CotaEventoRepository extends MongoRepository<CotaEvento, String> {

        @Query("{ ID_DOCUMENTO: ?0, ID_EMPRESA: ?1, ID_TIPO_DOCUMENTO: ?2, ID_UNIDADE_NEGOCIO: ?3, ID_COMISSIONADO: ?4 }")
        List<CotaEvento> findByDocumento(String idDocumento, String idEmpresa, String idTipoDocumento, String idUnidade, String idComissionado, Pageable pageable);

}
