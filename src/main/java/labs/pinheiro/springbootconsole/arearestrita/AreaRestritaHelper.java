package labs.pinheiro.springbootconsole.arearestrita;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import labs.pinheiro.springbootconsole.arearestrita.dto.CotaTimelineDTO;
import labs.pinheiro.springbootconsole.arearestrita.entity.Cota;
import labs.pinheiro.springbootconsole.arearestrita.exception.NotFoundInfoException;
import labs.pinheiro.springbootconsole.arearestrita.service.TimelineService;

@Component
public class AreaRestritaHelper {
    
    @Autowired
    private TimelineService timelineService;

    /*
     * Le arquivo, identifica idDocumento, consulta timeline da cota e identifica quais cotas estao com ultimo evento igual ao evento passado por parametro.
     * Gera novo arquivo contendo apenas as cotas no padrao identificado.
     */
    public void gerarCotasComUltimoEventoTimelinePorArquivo(String arquivo, String evento, Boolean isDplTopic) throws Exception {

        Path path = Paths.get("src/main/resources/" + arquivo);

        Path pathNew = Paths.get("src/main/resources/new" + arquivo);

        boolean isFirst = true;
    
        for (String line : Files.readAllLines(path)) {

            final String idDocumento = getField(line, "ID_DOCUMENTO");
            final String idEmpresa = getField(line, "ID_EMPRESA");
            final String idTipoDocumento = getField(line, "ID_TIPO_DOCUMENTO");
            final String idUnidade = getField(line, "ID_UNIDADE_NEGOCIO");
            final String idComissionado = getField(line, "ID_COMISSIONADO");
            final String dtProcessado = getField(line, "DT_PROCESSADO");
            final String dtProcessadoFmt = dtProcessado.substring(6) + "-" + dtProcessado.substring(3,5) +  "-" + dtProcessado.substring(0,2);
            final String dtProcessadoNew = "\"DT_PROCESSADO\": \\{\"string\": \"" + dtProcessadoFmt + "T00:00:00Z\"\\}";
            final String lineDtProcessadoReplaced = line.replaceAll("\"DT_PROCESSADO\": \\{\"string\": \"" + dtProcessado + "\"\\}", dtProcessadoNew);
            
            // aplicar filtros
            final String dtOperacao = getField(line, "DT_OPERACAO");
            if (dtOperacao.contains("23-05-2025") || dtOperacao.contains("24-05-2025") || dtOperacao.contains("25-05-2025") || dtOperacao.contains("26-05-2025")
                || dtOperacao.contains("27-05-2025") || dtOperacao.contains("28-05-2025") || dtOperacao.contains("29-05-2025") || dtOperacao.contains("30-05-2025")
                || dtOperacao.contains("31-05-2025") || dtOperacao.contains("01-06-2025") || dtOperacao.contains("02-06-2025")) {
                // Ok
            } else {
                continue;
            }

            try {
                CotaTimelineDTO dto = this.timelineService.getTimelineCotaByDocumento(idDocumento, idEmpresa, idTipoDocumento, idUnidade, idComissionado, "todos", null);
                if (evento.equalsIgnoreCase(dto.getTimeline().get(0).getEvento())) {

                    // Validar somente as cotas que estao com status diferente do ultimo evento da timeline
                    final Cota cota = this.timelineService.getCotaByIdCota(dto.getCota().getIdCota());
                    if (cota != null && cota.getEvento().equalsIgnoreCase(evento)) continue;
                   
                    // Preparar payload de envio
                    String lineStr = dto.getTimeline().get(0).getIdEvento() + ":" + line.substring(line.indexOf(":") + 1) + "\n";
                    if (!isDplTopic) {
                        lineStr = dto.getTimeline().get(0).getIdEvento() + ":" + lineDtProcessadoReplaced.substring(lineDtProcessadoReplaced.indexOf(":") + 1) + "\n";
                    } 

                    if (!isFirst) {
                        Files.writeString(pathNew, lineStr, StandardOpenOption.APPEND);
                    }

                    if (isFirst) {
                        Files.writeString(pathNew, lineStr);    
                        isFirst = false;
                    } 

                }
            } catch (NotFoundInfoException e) {
                // Ok, nada aqui.
            }

        }
        
    }

    private String getField(String line, String field) {
        Pattern pattern = Pattern.compile("(?:\\\"(?:"+field+")\\\":(?:[^:])*):(?:(?:[^\\\",])*)\\\"([^\\\"]*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
