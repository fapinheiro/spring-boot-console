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
public class EventoCotaInadimplenteDTO extends EventoDTO {
    
    private String idSituacaoCobranca;

    public EventoCotaInadimplenteDTO(Integer id, String evento, LocalDateTime dataEvento, String idSituacaoCobranca, String idEvento) {
        super(id, evento, dataEvento, idEvento);
        this.idSituacaoCobranca = idSituacaoCobranca;
    }
    
}
