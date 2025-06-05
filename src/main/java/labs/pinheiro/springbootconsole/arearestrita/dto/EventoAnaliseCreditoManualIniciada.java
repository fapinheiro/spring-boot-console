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
public class EventoAnaliseCreditoManualIniciada extends EventoDTO {
    
    private String prazo;
    private String responsavel;

    public EventoAnaliseCreditoManualIniciada(Integer id, String evento, LocalDateTime dataEvento, String prazo, String responsavel, String idEvento) {
        super(id, evento, dataEvento, idEvento);
        this.prazo = prazo;
        this.responsavel = responsavel;
    }
    
}
