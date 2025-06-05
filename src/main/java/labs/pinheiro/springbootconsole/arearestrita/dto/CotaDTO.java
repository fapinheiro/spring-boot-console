package labs.pinheiro.springbootconsole.arearestrita.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import labs.pinheiro.springbootconsole.arearestrita.entity.Cota;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CotaDTO implements Serializable {

    private static final DateTimeFormatter FORMAT_OUTPUT = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @JsonIgnore
    private String id;
    private String idDocumento;
    private String idEmpresa;
    private String idTipoDocumento;
    private String nome;
    private String grupo;
    private String cota;
    private String status;
    private String idCota;
    private String ddd;
    private String telefone;
    private String email;
    private LocalDateTime dataEvento; // *
    private String dataAtualizacao;
    private String loginComissionado;
    private String idComissionado;
    private String cpfCnpj;
    private FaturamentoBemAutoDTO faturamentoBemAuto;

    public CotaDTO(Cota cota) {
        this.idDocumento = cota.getIdDocumento();
        this.idEmpresa = cota.getIdEmpresa();
        this.idTipoDocumento = cota.getIdTipoDocumento();
        this.nome = cota.getNome();
        this.grupo = cota.getGrupo();
        this.cota = cota.getCota();
        this.status = cota.getEvento();
        this.idCota = cota.getIdCota();
        this.ddd = cota.getDdd();
        this.telefone = cota.getDdd() + cota.getTelefone();
        this.email = cota.getEmail();
        this.dataEvento = cota.getDataEvento();
        try {
            this.dataAtualizacao = cota.getDataEvento().format(FORMAT_OUTPUT);
        } catch (Exception e) {
            // Ok, nada aqui
        }
        this.loginComissionado = cota.getLoginComissionado();
        this.idComissionado = cota.getIdComissionado();
        this.cpfCnpj = cota.getCpfCnpj();
        if (cota.getFaturamentoBemAuto() != null) {
            this.faturamentoBemAuto = FaturamentoBemAutoDTO.builder()
                    .idCaso(cota.getFaturamentoBemAuto().getIdCaso())
                    .caso(cota.getFaturamentoBemAuto().getCaso())
                    .fase(cota.getFaturamentoBemAuto().getFase())
                    .status(cota.getFaturamentoBemAuto().getStatus())
                    .evento(cota.getFaturamentoBemAuto().getEvento())
                    .observacao(cota.getFaturamentoBemAuto().getObservacao())
                    .flagBemNovo("1".equals(cota.getFaturamentoBemAuto().getFlagBemNovo()) ? true : false)
                    .dataEvento(cota.getFaturamentoBemAuto().getDataEvento())
                    .flagIniciarProcesso(
                            "1".equals(cota.getFaturamentoBemAuto().getFlagIniciarProcesso()) ? true : false)
                    .build();
        }
    }
}
