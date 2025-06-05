package labs.pinheiro.springbootconsole.arearestrita.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CotaTimelineDTO implements Serializable {
    
    
    private CotaEventoDTO cota;
    private List<? extends EventoDTO> timeline;
    
}
