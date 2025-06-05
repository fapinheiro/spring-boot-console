package labs.pinheiro.springbootconsole.arearestrita.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoDTO implements Serializable {
    
    private static final DateTimeFormatter FORMAT_OUTPUT = DateTimeFormatter.ofPattern( "dd-MM-yyyy" );
    
    private Integer id;
    private String evento;
    private String dataEvento;
    private String idEvento;
    private String idCaso;
    private Boolean flagExibirMaisEventos;
    private List<EventoDTO> eventos;

    public EventoDTO(Integer id, String evento, LocalDateTime dataEvento, String idEvento) {
        this.id = id;
        this.evento = evento;
        try {
            this.dataEvento = dataEvento.plusDays(1).format(FORMAT_OUTPUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.idEvento = idEvento;
    }

    public EventoDTO(Integer id, String evento, LocalDateTime dataEvento, String idEvento, String idCaso) {
        this(id, evento, dataEvento, idEvento);
        this.idCaso = idCaso;
    }

    
}
