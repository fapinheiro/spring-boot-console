package labs.pinheiro.springbootconsole.arearestrita.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoFaturamentoBemAutoDTO extends EventoDTO {
    
    private String idCaso;
    private String caso;
    private String fase;
    private String observacao;
    private String status;
    private Boolean flagBemNovo;

    public EventoFaturamentoBemAutoDTO(Integer id, String evento, LocalDateTime dataEvento, String idEvento, String idCaso, String caso, String fase, String status, String observacao, String tipoProcesso) {
        super(id, evento, dataEvento, idEvento);
        this.idCaso = idCaso;
        this.caso = caso;
        this.fase = fase;
        this.status = status;
        this.observacao = observacao;
        this.flagBemNovo = "novo".equalsIgnoreCase(tipoProcesso);
    }
    
}
