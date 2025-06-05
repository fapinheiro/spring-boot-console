package labs.pinheiro.springbootconsole.arearestrita.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document( collection = "cota_evento")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class CotaEvento implements Comparable<CotaEvento> {
    
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
    @EqualsAndHashCode.Include
    private String status;

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

    @Field("DT_OPERACAO")
    @EqualsAndHashCode.Include
    private String dataOperacao;

    @Field("CD_USUARIO_EXTERNO")
    private String loginComissionado;

    @Field("ID_COMISSIONADO")
    private String idComissionado;

    @Field("CD_INSCRICAO_NACIONAL")
    private String cpfCnpj;

    @Field("DS_DOCUMENTOS_EXTRAS")
    @EqualsAndHashCode.Include
    private String documentosExtras;

    @Field("DS_EXPLIQUE_MOTIVO")
    @EqualsAndHashCode.Include
    private String expliqueMotivo;

    @Field("ID_CASO")
    private String idCaso;

    @Field("ID_MOTIVO_CANCELAMENTO")
    private String idMotivoCancelamento;

    @Field("ID_SITUACAO_COBRANCA")
    private String idSituacaoCobrancao;

    @Field("OBSERVACAO")
    @EqualsAndHashCode.Include
    private String observacao;

    @Field("TIPO_PROCESSO")
    @EqualsAndHashCode.Include
    private String tipoProcesso;

    @Field("DS_STATUS")
    @EqualsAndHashCode.Include
    private String dsStatus;

    @Field("DS_CASO")
    @EqualsAndHashCode.Include
    private String caso;

    @Field("DS_FASE")
    @EqualsAndHashCode.Include
    private String fase;

    @Field("ORIGEM")
    @EqualsAndHashCode.Include
    private String origem;

    @Override
    public int compareTo(CotaEvento o) {
       return this.status.compareTo(o.getStatus());
    }
  

}
