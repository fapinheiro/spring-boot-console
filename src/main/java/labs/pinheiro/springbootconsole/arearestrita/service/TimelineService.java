package labs.pinheiro.springbootconsole.arearestrita.service;

import labs.pinheiro.springbootconsole.arearestrita.dto.CotaTimelineDTO;
import labs.pinheiro.springbootconsole.arearestrita.entity.Cota;

public interface TimelineService {

    CotaTimelineDTO getTimelineCotaByDocumento(String idDocumento, String idEmpresa, String idTipoDocumento, String idUnidade, String idComissionado, String eventos, String idCasoFaturamentoBemAuto) throws Exception;
    Cota getCotaByIdCota(String idCota);
}
