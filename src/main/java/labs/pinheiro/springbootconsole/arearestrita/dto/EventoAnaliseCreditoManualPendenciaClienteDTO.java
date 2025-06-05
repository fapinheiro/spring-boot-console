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
public class EventoAnaliseCreditoManualPendenciaClienteDTO extends EventoDTO {

    private String prazo;
    private String pendencia;
    private String responsavel;
    private Boolean flagAtualizarCaso;
    private String idCaso;
    private String fase;

    public EventoAnaliseCreditoManualPendenciaClienteDTO(Integer id, String evento, LocalDateTime dataEvento,
            String prazo, String pendencia, String responsavel, Boolean flagAtualizarCaso, String idCaso, String fase, String idEvento) {
        super(id, evento, dataEvento, idEvento);
        this.prazo = prazo;
        this.pendencia = pendencia;
        this.responsavel = responsavel;
        this.flagAtualizarCaso = flagAtualizarCaso;
        this.idCaso = idCaso;
        this.fase = fase;
    }

}
