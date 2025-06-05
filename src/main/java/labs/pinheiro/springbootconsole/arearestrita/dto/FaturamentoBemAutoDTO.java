package labs.pinheiro.springbootconsole.arearestrita.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FaturamentoBemAutoDTO {
    
   
    private String idCaso;

    private String caso;
    
    private String fase;

    private String status;
    
    private String evento;

    private String observacao;

    private Boolean flagBemNovo;

    private LocalDateTime dataEvento;

    private Boolean flagIniciarProcesso;

}
