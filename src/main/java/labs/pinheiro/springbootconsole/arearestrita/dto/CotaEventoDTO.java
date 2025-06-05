package labs.pinheiro.springbootconsole.arearestrita.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import labs.pinheiro.springbootconsole.arearestrita.entity.CotaEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotaEventoDTO implements Serializable {


    private static final DateTimeFormatter FORMAT_OUTPUT = java.time.format.DateTimeFormatter.ofPattern( "dd-MM-yyyy" );

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
    private LocalDateTime dataEvento; //*
    private String dataAtualizacao;
    private String loginComissionado;
    private String idComissionado;
    private String cpfCnpj;
    
    public CotaEventoDTO(CotaEvento cota) {
        this.idDocumento = cota.getIdDocumento();
        this.idEmpresa = cota.getIdEmpresa();
        this.idTipoDocumento = cota.getIdTipoDocumento();
        this.nome = cota.getNome();
        this.grupo = cota.getGrupo();
        this.cota = cota.getCota();
        this.status = cota.getStatus();
        this.idCota = cota.getIdCota();
        this.ddd = cota.getDdd();
        this.telefone = cota.getDdd() + cota.getTelefone();
        this.email = cota.getEmail();
        this.dataEvento = cota.getDataEvento();
        try {
            this.dataAtualizacao = cota.getDataEvento().plusDays(1).format(FORMAT_OUTPUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.loginComissionado = cota.getLoginComissionado();
        this.idComissionado = cota.getIdComissionado();
        this.cpfCnpj = cota.getCpfCnpj();
    }
}
