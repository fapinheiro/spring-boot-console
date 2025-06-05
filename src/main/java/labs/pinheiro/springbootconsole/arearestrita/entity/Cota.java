package labs.pinheiro.springbootconsole.arearestrita.entity;

import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document( collection = "cota")
public class Cota {
    
    @Id
    private String id;

    @Field("ID_DOCUMENTO")
    private String idDocumento;

    @Field("ID_EMPRESA")
    private String idEmpresa;
    
    @Field("ID_TIPO_DOCUMENTO")
    private String idTipoDocumento;
    
    @Field("NM_CONSORCIADO")
    private String nome;

    @Field("CD_COTA")
    private String cota;

    @Field("CD_GRUPO")
    private String grupo;

    @Field("DS_EVENTO")
    private String evento;

    @Field("ID_COTA")
    private String idCota;

    @Field("DDD")
    private String ddd;

    @Field("TELEFONE")
    private String telefone;

    @Field("E_MAIL")
    private String email;

    @Field("DT_EVENTO")
    private LocalDateTime dataEvento;

    @Field("CD_USUARIO_EXTERNO")
    private String loginComissionado;

    @Field("ID_COMISSIONADO")
    private String idComissionado;

    @Field("CD_INSCRICAO_NACIONAL")
    private String cpfCnpj;

    @Field("DS_EXPLIQUE_MOTIVO")
    private String motivo;

    @Field("DS_DOCUMENTOS_EXTRAS")
    private String extras;

    @Field("ID_CASO")
    private String idCaso;

    @Field("FATURAMENTO_BEM_AUTO")
    private FaturamentoBemAuto faturamentoBemAuto;

}
