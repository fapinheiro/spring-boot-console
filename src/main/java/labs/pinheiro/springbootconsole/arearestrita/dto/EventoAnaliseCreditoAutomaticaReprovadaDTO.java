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
public class EventoAnaliseCreditoAutomaticaReprovadaDTO extends EventoDTO {

    private Boolean flagLiberarCriacaoCaso;
    private String fase;

    public EventoAnaliseCreditoAutomaticaReprovadaDTO(Integer id, String evento, LocalDateTime dataEvento,
            Boolean flagLiberarCriacaoCaso, String fase, String idEvento) {
        super(id, evento, dataEvento, idEvento);
        this.flagLiberarCriacaoCaso = flagLiberarCriacaoCaso;
        this.fase = fase;
    }

}
