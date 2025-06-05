package labs.pinheiro.springbootconsole.arearestrita.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Field;

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
public class FaturamentoBemAuto {
    

    @Field("ID_CASO")
    private String idCaso;

    @Field("DS_CASO")
    private String caso;
    
    @Field("DS_FASE")
    private String fase;

    @Field("DS_STATUS")
    private String status;
    
    @Field("DS_EVENTO")
    private String evento;

    @Field("DS_OBSERVACAO")
    private String observacao;

    @Field("ST_BEM_NOVO")
    private String flagBemNovo;

    @Field("DT_EVENTO")
    private LocalDateTime dataEvento;

    @Field("ST_INICIAR_PROCESSO")
    private String flagIniciarProcesso;

}
