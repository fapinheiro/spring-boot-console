package labs.pinheiro.springbootconsole.arearestrita.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import labs.pinheiro.springbootconsole.arearestrita.entity.Cota;

@Repository
public interface CotaRepository extends MongoRepository<Cota, String> {

        @Query("{ 'ID_COTA': ?0 }")
        List<Cota> findFirstCota(String idCota);

       
}
