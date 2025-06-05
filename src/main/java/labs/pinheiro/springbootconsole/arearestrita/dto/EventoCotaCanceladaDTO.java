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
public class EventoCotaCanceladaDTO extends EventoDTO {
    
    private String idMotivoCancelamento;

    public EventoCotaCanceladaDTO(Integer id, String evento, LocalDateTime dataEvento, String idMotivoCancelamento, String idEvento) {
        super(id, evento, dataEvento, idEvento);
        this.idMotivoCancelamento = idMotivoCancelamento;
    }
    
}
