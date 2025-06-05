package labs.pinheiro.springbootconsole.arearestrita.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import labs.pinheiro.springbootconsole.arearestrita.repository.*;
import labs.pinheiro.springbootconsole.arearestrita.exception.NotFoundInfoException;
import labs.pinheiro.springbootconsole.arearestrita.util.Util;
import labs.pinheiro.springbootconsole.arearestrita.dto.CotaEventoDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.CotaTimelineDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoAnaliseCreditoAutomaticaReprovadaDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoAnaliseCreditoManualIniciada;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoAnaliseCreditoManualPendenciaClienteDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoCotaCanceladaDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoCotaInadimplenteDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoCotaQuitadaDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoFaturamentoBemAutoDTO;
import labs.pinheiro.springbootconsole.arearestrita.dto.EventoPagamentoLanceDTO;
import labs.pinheiro.springbootconsole.arearestrita.entity.Cota;
import labs.pinheiro.springbootconsole.arearestrita.entity.CotaEvento;
import labs.pinheiro.springbootconsole.arearestrita.service.TimelineService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TimelineServiceImpl implements TimelineService {

    // Fases do Salesforce
    public static final String TRATATIVA_NAO_INICIADA = "Tratativa não Iniciada";
    public static final String TRIAGEM_EM_ANDAMENTO = "Triagem em Andamento";
    public static final String RETORNO_CLIENTE = "Retorno Cliente";

    // Formato padrao de data
    private static final String DATA_FORMAT = "dd-MM-yyyy";

    // Eventos de analise credito manual
    public static final String ANALISE_CREDITO_MANUAL_CADASTRADA = "analise_credito_manual_cadastrada";
    public static final String ANALISE_CREDITO_MANUAL_INICIADA = "analise_credito_manual_iniciada";
    public static final String ANALISE_CREDITO_MANUAL_AGUARDANDO_CLIENTE = "analise_credito_manual_aguardando_cliente";
    public static final String ANALISE_CREDITO_MANUAL_APROVADA = "analise_credito_manual_aprovada";
    public static final String ANALISE_CREDITO_MANUAL_REPROVADA = "analise_credito_manual_reprovada";
    public static final String ANALISE_CREDITO_MANUAL_REANALISE_INICIADA = "analise_credito_manual_reanalise_iniciada";
    public static final String ANALISE_CREDITO_MANUAL_ = "analise_credito_manual_";
    public static final List<String> ANALISE_CREDITO_MANUAL_LIST = List.of(
            ANALISE_CREDITO_MANUAL_CADASTRADA,
            ANALISE_CREDITO_MANUAL_INICIADA,
            ANALISE_CREDITO_MANUAL_AGUARDANDO_CLIENTE,
            ANALISE_CREDITO_MANUAL_APROVADA,
            ANALISE_CREDITO_MANUAL_REPROVADA,
            ANALISE_CREDITO_MANUAL_REANALISE_INICIADA);

    // Eventos de analise credito automatica
    public static final String ANALISE_CREDITO_AUTOMATICA_APROVADA = "analise_credito_automatica_aprovada";
    public static final String ANALISE_CREDITO_AUTOMATICA_REPROVADA = "analise_credito_automatica_reprovada";
    public static final String ANALISE_CREDITO_AUTOMATICA_ = "analise_credito_automatica_";
    public static final List<String> ANALISE_CREDITO_AUTOMATICA_LIST = List.of(
            ANALISE_CREDITO_AUTOMATICA_APROVADA,
            ANALISE_CREDITO_AUTOMATICA_REPROVADA);

    // Eventos de aviso contemplacao
    public static final String AVISO_CONTEMPLACAO_ENVIADO = "aviso_contemplacao_enviado";
    public static final String AVISO_ = "aviso_";
    public static final List<String> AVISO_LIST = List.of(
            AVISO_CONTEMPLACAO_ENVIADO);

    // Eventos de cota
    public static final String COTA_CONTEMPLADA_SORTEIO = "cota_contemplada_sorteio";
    public static final String COTA_CONTEMPLADA_LANCE = "cota_contemplada_lance";
    public static final String COTA_DESCLASSIFICADA = "cota_desclassificada";
    public static final String COTA_ALOCADA = "cota_alocada";
    public static final String COTA_CANCELADA = "cota_cancelada";
    public static final String COTA_QUITADA = "cota_quitada";
    public static final String COTA_INADIMPLENTE = "cota_inadimplente";
    public static final String COTA_REATIVADA = "cota_reativada";
    public static final String COTA_ADIMPLENTE = "cota_adimplente";
    public static final String COTA_ = "cota_";
    public static final List<String> COTA_LIST = List.of(
            COTA_CONTEMPLADA_SORTEIO,
            COTA_CONTEMPLADA_LANCE,
            COTA_DESCLASSIFICADA,
            COTA_ALOCADA,
            COTA_CANCELADA,
            COTA_QUITADA,
            COTA_INADIMPLENTE,
            COTA_REATIVADA,
            COTA_ADIMPLENTE);

    // Eventos de faturamento bem auto
    public static final String FATURAMENTO_BEM_AUTO_CADASTRADO = "faturamento_bem_auto_cadastrado";
    public static final String FATURAMENTO_BEM_AUTO_ANALISE_INICIADA = "faturamento_bem_auto_analise_iniciada";
    public static final String FATURAMENTO_BEM_AUTO_AGUARDANDO_CLIENTE = "faturamento_bem_auto_aguardando_cliente";
    public static final String FATURAMENTO_BEM_AUTO_AGUARDANDO_ESCOLHA_BEM = "faturamento_bem_auto_aguardando_escolha_bem";
    public static final String FATURAMENTO_BEM_AUTO_AUTENTICACAO_INICIADA = "faturamento_bem_auto_autenticacao_iniciada";
    public static final String FATURAMENTO_BEM_AUTO_AUTENTICACAO_REANALISE_INICIADA = "faturamento_bem_auto_autenticacao_reanalise_iniciada";
    public static final String FATURAMENTO_BEM_AUTO_AGUARDANDO_DOCUMENTO = "faturamento_bem_auto_aguardando_documento";
    public static final String FATURAMENTO_BEM_AUTO_ANALISE_GRC_INICIADA = "faturamento_bem_auto_analise_grc_iniciada";
    public static final String FATURAMENTO_BEM_AUTO_DOCUMENTO_RECUSADO = "faturamento_bem_auto_documento_recusado";
    public static final String FATURAMENTO_BEM_AUTO_SEM_INTERESSE = "faturamento_bem_auto_sem_interesse";
    public static final String FATURAMENTO_BEM_AUTO_AGUARDANDO_VISTORIA = "faturamento_bem_auto_aguardando_vistoria";
    public static final String FATURAMENTO_BEM_AUTO_BEM_ENCONTRADO = "faturamento_bem_auto_bem_encontrado";
    public static final String FATURAMENTO_BEM_AUTO_VISTORIA_ERRO = "faturamento_bem_auto_vistoria_erro";
    public static final String FATURAMENTO_BEM_AUTO_VISTORIA_AGENDADA = "faturamento_bem_auto_vistoria_agendada";
    public static final String FATURAMENTO_BEM_AUTO_VEICULO_RECUSADO = "faturamento_bem_auto_veiculo_recusado";
    public static final String FATURAMENTO_BEM_AUTO_AGUARDANDO_VEICULO = "faturamento_bem_auto_aguardando_veiculo";
    public static final String FATURAMENTO_BEM_AUTO_DOCUMENTO_RECEBIDO = "faturamento_bem_auto_documento_recebido";
    public static final String FATURAMENTO_BEM_AUTO_GARANTIA_APROVADA = "faturamento_bem_auto_garantia_aprovada";
    public static final String FATURAMENTO_BEM_AUTO_INDICACAO_ERRO = "faturamento_bem_auto_indicacao_erro";
    public static final String FATURAMENTO_BEM_AUTO_DADOS_CADASTRADOS = "faturamento_bem_auto_dados_cadastrados";
    public static final String FATURAMENTO_BEM_AUTO_PAGAMENTO_INICIADO = "faturamento_bem_auto_pagamento_iniciado";
    public static final String FATURAMENTO_BEM_AUTO_AGUARDANDO_DETRAN = "faturamento_bem_auto_aguardando_detran";
    public static final String FATURAMENTO_BEM_AUTO_ANALISE_CANAL_INICIADA = "faturamento_bem_auto_analise_canal_iniciada";
    public static final String FATURAMENTO_BEM_AUTO_PAGAMENTO_ENVIADO = "faturamento_bem_auto_pagamento_enviado";
    public static final String FATURAMENTO_BEM_AUTO_SALDO_INSUFICIENTE = "faturamento_bem_auto_saldo_insuficiente";
    public static final String FATURAMENTO_BEM_AUTO_DOCUMENTO_ASSINADO = "faturamento_bem_auto_documento_assinado";
    public static final String FATURAMENTO_BEM_AUTO_PRAZO_EXPIRADO = "faturamento_bem_auto_prazo_expirado";
    public static final String FATURAMENTO_BEM_AUTO_ = "faturamento_bem_auto_";
    public static final List<String> FATURAMENTO_BEM_AUTO_LIST = List.of(
            FATURAMENTO_BEM_AUTO_CADASTRADO,
            FATURAMENTO_BEM_AUTO_ANALISE_INICIADA,
            FATURAMENTO_BEM_AUTO_AGUARDANDO_CLIENTE,
            FATURAMENTO_BEM_AUTO_AGUARDANDO_ESCOLHA_BEM,
            FATURAMENTO_BEM_AUTO_AUTENTICACAO_INICIADA,
            FATURAMENTO_BEM_AUTO_AUTENTICACAO_REANALISE_INICIADA,
            FATURAMENTO_BEM_AUTO_AGUARDANDO_DOCUMENTO,
            FATURAMENTO_BEM_AUTO_ANALISE_GRC_INICIADA,
            FATURAMENTO_BEM_AUTO_DOCUMENTO_RECUSADO,
            FATURAMENTO_BEM_AUTO_SEM_INTERESSE,
            FATURAMENTO_BEM_AUTO_AGUARDANDO_VISTORIA,
            FATURAMENTO_BEM_AUTO_BEM_ENCONTRADO,
            FATURAMENTO_BEM_AUTO_VISTORIA_ERRO,
            FATURAMENTO_BEM_AUTO_VISTORIA_AGENDADA,
            FATURAMENTO_BEM_AUTO_VEICULO_RECUSADO,
            FATURAMENTO_BEM_AUTO_AGUARDANDO_VEICULO,
            FATURAMENTO_BEM_AUTO_DOCUMENTO_RECEBIDO,
            FATURAMENTO_BEM_AUTO_GARANTIA_APROVADA,
            FATURAMENTO_BEM_AUTO_INDICACAO_ERRO,
            FATURAMENTO_BEM_AUTO_DADOS_CADASTRADOS,
            FATURAMENTO_BEM_AUTO_ANALISE_CANAL_INICIADA,
            FATURAMENTO_BEM_AUTO_SALDO_INSUFICIENTE,
            FATURAMENTO_BEM_AUTO_PAGAMENTO_INICIADO,
            FATURAMENTO_BEM_AUTO_AGUARDANDO_DETRAN,
            FATURAMENTO_BEM_AUTO_PAGAMENTO_ENVIADO,
            FATURAMENTO_BEM_AUTO_DOCUMENTO_ASSINADO,
            FATURAMENTO_BEM_AUTO_PRAZO_EXPIRADO);

    // Eventos de pagamento
    public static final String PAGAMENTO_LANCE_REALIZADO = "pagamento_lance_realizado";
    public static final String PAGAMENTO_LANCE_AGUARDANDO_CLIENTE = "pagamento_lance_aguardando_cliente";
    public static final String PAGAMENTO_BEM_TOTAL = "pagamento_bem_total";
    public static final String PAGAMENTO_ = "pagamento_";
    public static final List<String> PAGAMENTO_LIST = List.of(
            PAGAMENTO_LANCE_REALIZADO,
            PAGAMENTO_LANCE_AGUARDANDO_CLIENTE,
            PAGAMENTO_BEM_TOTAL);

    // Evento de entrada da venda
    public static final String PROPOSTA_CADASTRADA = "proposta_cadastrada";
    public static final String PROPOSTA_ = "proposta_";
    public static final List<String> PROPOSTA_LIST = List.of(
            PROPOSTA_CADASTRADA);

    // Agrupamentos
    // Servem para agrupar os eventos na timeline de forma a evitar "fetch
    // overloading".
    public static final String FATURAMENTO_BEM_AUTO_AGRUPADO_POR_CASO = "faturamento_bem_auto_agrupado_por_caso";

    // Eventos que serao retornados por padrao quando na consulta da timeline nao
    // for informado/declarado os eventos de retorno.
    // Alguns eventos como o cota_contemplada_sorteio ou cota_contemplada_lance nao
    // podem ser exibidos na timeline, exceto se na apresentação em tela o front
    // exibir algo
    // como
    // "aguardando confirmação de contemplação" pois estes eventos refletem o
    // resultado
    // da assembleia que posteriormente poderá ser desclassificada.
    public static final List<String> EVENTOS_DEFAULT = List.of(
            COTA_ALOCADA,
            COTA_CONTEMPLADA_SORTEIO,
            COTA_CONTEMPLADA_LANCE,
            AVISO_CONTEMPLACAO_ENVIADO,
            PAGAMENTO_LANCE_REALIZADO,
            PAGAMENTO_LANCE_AGUARDANDO_CLIENTE,
            ANALISE_CREDITO_AUTOMATICA_APROVADA,
            ANALISE_CREDITO_AUTOMATICA_REPROVADA,
            ANALISE_CREDITO_MANUAL_INICIADA,
            ANALISE_CREDITO_MANUAL_REANALISE_INICIADA,
            ANALISE_CREDITO_MANUAL_REPROVADA,
            ANALISE_CREDITO_MANUAL_APROVADA,
            ANALISE_CREDITO_MANUAL_AGUARDANDO_CLIENTE,
            COTA_DESCLASSIFICADA,
            PAGAMENTO_BEM_TOTAL,
            COTA_CANCELADA,
            COTA_QUITADA,
            COTA_INADIMPLENTE,
            COTA_ADIMPLENTE,
            COTA_REATIVADA,
            FATURAMENTO_BEM_AUTO_SEM_INTERESSE,
            FATURAMENTO_BEM_AUTO_PAGAMENTO_ENVIADO,
            FATURAMENTO_BEM_AUTO_DOCUMENTO_RECUSADO);

    @Value("${info.app.data-golive}")
    private String dataGolive;

    @Autowired
    private CotaEventoRepository cotaEventoRepository;

    @Autowired
    private CotaRepository cotaRepository;

    @Override
    // @Cacheable(value = "timelinePorDocumentoCache")
    public CotaTimelineDTO getTimelineCotaByDocumento(String idDocumento, String idEmpresa,
            String idTipoDocumento, String idUnidade, String idComissionado, String eventosFiltro,
            String idCasoFaturamentoBemAuto) throws Exception {

        // Obter todos os eventos da timeline
        List<CotaEvento> eventos = cotaEventoRepository.findByDocumento(idDocumento, idEmpresa,
                idTipoDocumento, idUnidade,
                idComissionado, getPageableAsc("200", "1"));
        log.debug("eventos {}", eventos);

        // Remover eventos duplicados
        // Eventos duplicados podem acontecer mesmo com a feature
        // de exactly once do kafka.
        // Podem ocorrer quando o sistema de origem faz um novo disparo do mesmo evento.
        // É necessario implementar o Equals e HashCode na classe CotaEvento.
        List<CotaEvento> eventosNaoDuplicados = new ArrayList<>();
        for (int i = 0; i < eventos.size(); i++) {
            CotaEvento e = eventos.get(i);
            if (eventosNaoDuplicados.contains(e)) {
                // Se já contem evento, só nao deixa duplicar se for o ultimo
                if (!eventosNaoDuplicados.get(eventosNaoDuplicados.size() - 1).getStatus().equals(e.getStatus())) {
                    eventosNaoDuplicados.add(e);
                }
            } else {
                eventosNaoDuplicados.add(e);
            }
        }
        log.debug("eventosNaoDuplicados {}", eventosNaoDuplicados);

        // Ordenar por data os eventos nao duplicados
        // Importante pois eventos podem chegar fora de ordem
        List<CotaEvento> eventosNaoDuplicadosOrdenados = new ArrayList<>();
        eventosNaoDuplicadosOrdenados.addAll(eventosNaoDuplicados);
        eventosNaoDuplicadosOrdenados.sort(new Comparator<CotaEvento>() {
            @Override
            public int compare(CotaEvento o1, CotaEvento o2) {
                return o1.getDataEvento().compareTo(o2.getDataEvento());
            }
        });
        log.debug("eventosNaoDuplicadosOrdenados {}", eventosNaoDuplicadosOrdenados);

        // Montar timeline baseado nas flags de sinalizaçao para montar uma estrutura
        // que seja visualmente melhor, sem repetiçoes.
        // Ver os cenários de teste para maiores detalhes e exemplos.
        boolean flagAvisoContemplacao = false;
        boolean flagPagamentoLanceRealizado = false;
        boolean flagAnaliseManualEmAndamento = false;
        boolean flagPagamentoBemTotal = false;
        boolean flagAnaliseManualCadastrada = false;
        boolean flagRenaliseManualIniciada = false;
        boolean flagAnaliseManualIniciada = false;
        boolean flagAnaliseManualAprovada = false;
        boolean flagAnaliseManualReprovada = false;
        boolean flagAnaliseAutomaticaReprovada = false;
        boolean flagAnaliseAutomaticaAprovada = false;
        boolean flagCotaCancelada = false;
        boolean flagCotaReativada = false;
        boolean flagCotaInadimplente = false;
        boolean flagCotaAdimplente = false;
        boolean flagCotaQuitada = false;
        boolean flagfaturamentoBemAutoCadastrado = false;
        boolean flagfaturamentoBemAutoAnaliseIniciada = false;
        boolean flagfaturamentoBemAutoDocumentoRecusado = false;
        boolean flagfaturamentoBemAutoAguardandoEscolhaBem = false;
        boolean flagfaturamentoBemAutoAguardandoDocumento = false;
        boolean flagfaturamentoBemAutoDocumentoRecebido = false;
        boolean flagfaturamentoBemAutoDocumentoAssinado = false;
        boolean flagfaturamentoBemAutoPagamentoEnviado = false;
        boolean flagfaturamentoBemAutoSemInteresse = false;
        boolean flagfaturamentoBemAutoPrazoExpirado = false;
        boolean flagfaturamentoBemAutoAnaliseGrcIniciada = false;
        boolean flagfaturamentoBemAutoPagamentoIniciado = false;
        boolean flagfaturamentoBemAutoAguardandoDetran = false;
        boolean flagfaturamentoBemAutoAguardandoCliente = false;
        boolean flagfaturamentoBemAutoAguardandoVistoria = false;
        boolean flagfaturamentoBemAutoAguardandoVeiculo = false;
        boolean flagfaturamentoBemAutoBemEncontrado = false;
        boolean flagfaturamentoBemAutoAutenticacaoIniciada = false;
        boolean flagfaturamentoBemAutoAutenticacaoReanaliseIniciada = false;
        boolean flagfaturamentoBemAutoVistoriaErro = false;
        boolean flagfaturamentoBemAutoVistoriaAgendada = false;
        boolean flagfaturamentoBemAutoVeiculoRecusado = false;
        boolean flagfaturamentoBemAutoGarantiaAprovada = false;
        boolean flagfaturamentoBemAutoIndicacaoErro = false;
        boolean flagfaturamentoBemAutoDadosCadastrados = false;
        boolean flagfaturamentoBemAutoAnaliseCanalIniciada = false;
        boolean flagfaturamentoBemAutoSaldoInsuficiente = false;

        Stack<EventoDTO> pilha = new Stack<>();
        for (int i = 0; i < eventosNaoDuplicadosOrdenados.size(); i++) {

            // Obter evento
            CotaEvento cota = eventosNaoDuplicadosOrdenados.get(i);

            // Tratar eventos customizados
            if (COTA_CONTEMPLADA_LANCE.equalsIgnoreCase(cota.getStatus())
                    || COTA_CONTEMPLADA_SORTEIO.equalsIgnoreCase(cota.getStatus())
                    || COTA_DESCLASSIFICADA.equalsIgnoreCase(cota.getStatus())) {

                flagAnaliseManualEmAndamento = false;
                flagAvisoContemplacao = false;
                flagAnaliseManualCadastrada = false;
                flagAnaliseAutomaticaReprovada = false;
                flagAnaliseAutomaticaAprovada = false;
                if (COTA_CONTEMPLADA_LANCE.equalsIgnoreCase(cota.getStatus())) {
                    flagPagamentoLanceRealizado = false;
                }
                pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));

            } else if (AVISO_CONTEMPLACAO_ENVIADO.equalsIgnoreCase(cota.getStatus())) {

                // Nao deixar duplicar aviso de contemplacao
                if (!flagAvisoContemplacao) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagAvisoContemplacao = true;
                }

                flagAnaliseAutomaticaReprovada = false;
                flagAnaliseAutomaticaAprovada = false;

            } else if (PAGAMENTO_LANCE_REALIZADO.equalsIgnoreCase(cota.getStatus())) {

                // Inserir apenas um evento.
                // Pode duplicar devido a lance embutido, diluido, estorno, etc
                // Mas aqui queremos apenas sinalizar que teve um pagamento
                if (!flagPagamentoLanceRealizado) {
                    pilha.push(new EventoPagamentoLanceDTO(null, cota.getStatus(),
                            cota.getDataEvento(), "5 dias", "Contemplação", cota.getId()));
                    flagPagamentoLanceRealizado = true;
                }

            } else if (ANALISE_CREDITO_MANUAL_REANALISE_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                if (!flagRenaliseManualIniciada) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagAnaliseManualEmAndamento = true;
                    flagRenaliseManualIniciada = true;
                }

            } else if (ANALISE_CREDITO_MANUAL_APROVADA.equalsIgnoreCase(cota.getStatus())) {

                if (!flagAnaliseManualAprovada) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagAnaliseManualEmAndamento = true;
                    flagAnaliseManualAprovada = true;
                    flagAnaliseManualReprovada = false;
                }

            } else if (ANALISE_CREDITO_MANUAL_REPROVADA.equalsIgnoreCase(cota.getStatus())) {

                if (!flagAnaliseManualReprovada) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagAnaliseManualEmAndamento = true;
                    flagAnaliseManualReprovada = true;
                    flagAnaliseManualAprovada = false;
                }

            } else if (ANALISE_CREDITO_MANUAL_AGUARDANDO_CLIENTE.equalsIgnoreCase(cota.getStatus())) {

                flagAnaliseManualEmAndamento = true;

                // Ignorar evento se for analise de credito pendencia cliente sem motivo
                // Motivo nao pode ser vazio ou nulo.
                final String motivo = cota.getExpliqueMotivo() != null ? cota.getExpliqueMotivo()
                        : cota.getDocumentosExtras();
                if (StringUtils.isBlank(motivo)) {
                    continue;
                }

                // Se é ultimo da lista liberar flag
                if (eventosNaoDuplicadosOrdenados.size() - 1 == i) {
                    pilha.push(new EventoAnaliseCreditoManualPendenciaClienteDTO(null, cota.getStatus(),
                            cota.getDataEvento(),
                            "5 dias", motivo, "Análise de Crédito", true,
                            cota.getIdCaso(), RETORNO_CLIENTE, cota.getId()));
                    continue;
                }

                // Localizar o index da proxima contemplação
                final Integer indexProximaContemplacao = getProximoIndex(i, eventosNaoDuplicadosOrdenados,
                        COTA_CONTEMPLADA_LANCE, COTA_CONTEMPLADA_SORTEIO);

                // Avaliar se a pendencia ainda esta ativa
                // Se tiver eventos que indicam que a pendencia ja foi enviada
                // nao liberar a flag
                final boolean flagAtualizarCaso = !temEventosInclusivo(i, indexProximaContemplacao,
                        eventosNaoDuplicadosOrdenados,
                        ANALISE_CREDITO_MANUAL_INICIADA, ANALISE_CREDITO_MANUAL_REANALISE_INICIADA,
                        ANALISE_CREDITO_MANUAL_APROVADA, ANALISE_CREDITO_MANUAL_REPROVADA);

                pilha.push(new EventoAnaliseCreditoManualPendenciaClienteDTO(null, cota.getStatus(),
                        cota.getDataEvento(),
                        "5 dias", motivo, "Análise de Crédito", flagAtualizarCaso,
                        cota.getIdCaso(), RETORNO_CLIENTE, cota.getId()));

                flagAnaliseManualIniciada = false;
                flagRenaliseManualIniciada = false;

            } else if (ANALISE_CREDITO_MANUAL_CADASTRADA.equalsIgnoreCase(cota.getStatus())) {

                flagAnaliseManualCadastrada = true;
                pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));

            } else if (ANALISE_CREDITO_AUTOMATICA_APROVADA.equalsIgnoreCase(cota.getStatus())) {

                // Nao deixar duplicar eventos em datas diferentes
                if (!flagAnaliseAutomaticaAprovada) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagAnaliseAutomaticaAprovada = true;
                    flagAnaliseAutomaticaReprovada = false;
                }

            } else if (ANALISE_CREDITO_AUTOMATICA_REPROVADA.equalsIgnoreCase(cota.getStatus())) {

                // Nao liberar abertura de casos para eventos antes do golive
                // Eventos antes do golive pode ja ter sido iniciados no Salesforce
                // Efeito colateral é sobrescrever os dados do caso em andamento
                final LocalDate dataImplantacao = LocalDate.parse(dataGolive,
                        DateTimeFormatter.ofPattern(DATA_FORMAT));
                final LocalDateTime dataTimeImplantacao = dataImplantacao.atTime(0, 0, 0);
                if (cota.getDataEvento().isBefore(dataTimeImplantacao)) {
                    pilha.push(new EventoAnaliseCreditoAutomaticaReprovadaDTO(
                            null, cota.getStatus(), cota.getDataEvento(),
                            false, TRATATIVA_NAO_INICIADA, cota.getId()));
                    continue;
                }

                // Liberar a flag somente se tem contemplacao confirmada e nao tem evento de
                // analise já em andamento.
                // Uma analise já em andamento nao é permitido a flag pois poderá duplicar ou
                // sobrescrever o caso já em andamento no Salesforce.
                boolean isFlagLiberarCriacaoCaso = flagAvisoContemplacao && !flagAnaliseManualEmAndamento;

                // Verificar se tem analise em andamento ou aprovacao
                final boolean temAnaliseEmAndamentoPosterior = temEventosInclusivo(i, -1,
                        eventosNaoDuplicadosOrdenados,
                        ANALISE_CREDITO_MANUAL_INICIADA, ANALISE_CREDITO_MANUAL_REANALISE_INICIADA,
                        ANALISE_CREDITO_MANUAL_APROVADA, ANALISE_CREDITO_MANUAL_REPROVADA,
                        ANALISE_CREDITO_AUTOMATICA_APROVADA);

                // Bloquear flag se aviso de contemplacao chegou antes mas tem analise em
                // andamento posterior.
                if (flagAvisoContemplacao && temAnaliseEmAndamentoPosterior) {
                    isFlagLiberarCriacaoCaso = false;
                }

                // Localizar se tem evento de analise cadastrada chegou depois
                final Integer indexProximoAnaliseManualCadastrada = getProximoIndex(i, eventosNaoDuplicadosOrdenados,
                        ANALISE_CREDITO_MANUAL_CADASTRADA);
                final boolean temaAnaliseManualCadastradaPosterior = indexProximoAnaliseManualCadastrada != -1;

                // Montar campo fase de abertura do caso no Salesforce
                final String fase = flagAnaliseManualCadastrada ? TRIAGEM_EM_ANDAMENTO
                        : temaAnaliseManualCadastradaPosterior && !temAnaliseEmAndamentoPosterior ? TRIAGEM_EM_ANDAMENTO
                                : TRATATIVA_NAO_INICIADA;

                // Nao deixar duplicar eventos em datas diferentes
                if (!flagAnaliseAutomaticaReprovada) {
                    pilha.push(new EventoAnaliseCreditoAutomaticaReprovadaDTO(
                            null, cota.getStatus(), cota.getDataEvento(),
                            isFlagLiberarCriacaoCaso, fase, cota.getId()));
                    flagAnaliseAutomaticaReprovada = true;
                    flagAnaliseAutomaticaAprovada = false;
                }

            } else if (ANALISE_CREDITO_MANUAL_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                flagAnaliseManualEmAndamento = true;

                if (!flagAnaliseManualIniciada) {
                    pilha.push(new EventoAnaliseCreditoManualIniciada(null, cota.getStatus(), cota.getDataEvento(),
                            "7 dias", "Analise de Crédito", cota.getId()));
                    flagAnaliseManualIniciada = true;
                }

            } else if (PAGAMENTO_BEM_TOTAL.equalsIgnoreCase(cota.getStatus())) {

                // Inserir apenas um evento
                // Pode se repetir quando o cliente solicita o pagamento parcial
                if (!flagPagamentoBemTotal) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagPagamentoBemTotal = true;
                }

            } else if (COTA_CANCELADA.equalsIgnoreCase(cota.getStatus())) {

                if (!flagCotaCancelada) {
                    pilha.push(new EventoCotaCanceladaDTO(null, cota.getStatus(), cota.getDataEvento(),
                            cota.getIdMotivoCancelamento(),
                            cota.getId()));
                    flagCotaCancelada = true;
                }

            } else if (COTA_QUITADA.equalsIgnoreCase(cota.getStatus())) {

                if (!flagCotaQuitada) {
                    pilha.push(
                            new EventoCotaQuitadaDTO(null, cota.getStatus(), cota.getDataEvento(),
                                    cota.getIdSituacaoCobrancao(), cota.getId()));
                    flagCotaQuitada = true;
                }

            } else if (COTA_INADIMPLENTE.equalsIgnoreCase(cota.getStatus())) {

                if (!flagCotaInadimplente) {
                    pilha.push(new EventoCotaInadimplenteDTO(null, cota.getStatus(), cota.getDataEvento(),
                            cota.getIdSituacaoCobrancao(),
                            cota.getId()));
                    flagCotaInadimplente = true;
                    flagCotaAdimplente = false;
                }

            } else if (COTA_REATIVADA.equalsIgnoreCase(cota.getStatus())) {

                if (!flagCotaReativada) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagCotaReativada = true;
                    flagCotaCancelada = false;
                }

            } else if (COTA_ADIMPLENTE.equalsIgnoreCase(cota.getStatus())) {

                if (!flagCotaAdimplente) {
                    pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
                    flagCotaAdimplente = true;
                    flagCotaInadimplente = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_CADASTRADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoCadastrado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoCadastrado = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_ANALISE_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAnaliseIniciada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAnaliseIniciada = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_DOCUMENTO_RECUSADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoDocumentoRecusado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoDocumentoRecusado = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_AGUARDANDO_ESCOLHA_BEM.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAguardandoEscolhaBem) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAguardandoEscolhaBem = true;
                    flagfaturamentoBemAutoAguardandoDocumento = false;
                    flagfaturamentoBemAutoAguardandoVeiculo = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_AGUARDANDO_DOCUMENTO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                List<String> fasesList = List.of("Pendência NF",
                        "Documentos Ilegíveis",
                        "Documentos Incorretos",
                        "Revalidar Vendedor",
                        "Docs Adicionais GRC");

                final boolean isFaseMatch = fasesList.stream().anyMatch(s -> s.equalsIgnoreCase(dto.getFase()));
                if (isFaseMatch) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoDocumentoRecebido = false;
                    continue;
                }

                if (!flagfaturamentoBemAutoAguardandoDocumento) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAguardandoDocumento = true;
                    flagfaturamentoBemAutoDocumentoRecebido = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_DOCUMENTO_RECEBIDO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoDocumentoRecebido) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoDocumentoRecebido = true;
                    flagfaturamentoBemAutoAguardandoDocumento = false;
                    flagfaturamentoBemAutoAguardandoVeiculo = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_DOCUMENTO_ASSINADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoDocumentoAssinado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoDocumentoAssinado = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_PAGAMENTO_INICIADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoPagamentoIniciado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoPagamentoIniciado = true;
                    flagfaturamentoBemAutoAguardandoDocumento = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_PAGAMENTO_ENVIADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoPagamentoEnviado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoPagamentoEnviado = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_SEM_INTERESSE.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoSemInteresse) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoSemInteresse = true;
                    flagfaturamentoBemAutoCadastrado = false;
                    flagfaturamentoBemAutoAnaliseIniciada = false;
                    flagfaturamentoBemAutoDocumentoRecusado = false;
                    flagfaturamentoBemAutoAguardandoEscolhaBem = false;
                    flagfaturamentoBemAutoAguardandoDocumento = false;
                    flagfaturamentoBemAutoDocumentoRecebido = false;
                    flagfaturamentoBemAutoDocumentoAssinado = false;
                    flagfaturamentoBemAutoPagamentoEnviado = false;
                    flagfaturamentoBemAutoSemInteresse = false;
                    flagfaturamentoBemAutoPrazoExpirado = false;
                    flagfaturamentoBemAutoAnaliseGrcIniciada = false;
                    flagfaturamentoBemAutoPagamentoIniciado = false;
                    flagfaturamentoBemAutoAguardandoDetran = false;
                    flagfaturamentoBemAutoAguardandoCliente = false;
                    flagfaturamentoBemAutoAguardandoVistoria = false;
                    flagfaturamentoBemAutoAguardandoVeiculo = false;
                    flagfaturamentoBemAutoAutenticacaoIniciada = false;
                    flagfaturamentoBemAutoAutenticacaoReanaliseIniciada = false;
                    flagfaturamentoBemAutoVistoriaAgendada = false;
                    flagfaturamentoBemAutoVistoriaErro = false;
                    flagfaturamentoBemAutoVeiculoRecusado = false;
                    flagfaturamentoBemAutoGarantiaAprovada = false;
                    flagfaturamentoBemAutoIndicacaoErro = false;
                    flagfaturamentoBemAutoDadosCadastrados = false;
                    flagfaturamentoBemAutoAnaliseCanalIniciada = false;
                    flagfaturamentoBemAutoSaldoInsuficiente = false;

                }

            } else if (FATURAMENTO_BEM_AUTO_PRAZO_EXPIRADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoPrazoExpirado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoPrazoExpirado = true;
                    flagfaturamentoBemAutoCadastrado = false;
                    flagfaturamentoBemAutoAnaliseIniciada = false;
                    flagfaturamentoBemAutoDocumentoRecusado = false;
                    flagfaturamentoBemAutoAguardandoEscolhaBem = false;
                    flagfaturamentoBemAutoAguardandoDocumento = false;
                    flagfaturamentoBemAutoDocumentoRecebido = false;
                    flagfaturamentoBemAutoDocumentoAssinado = false;
                    flagfaturamentoBemAutoPagamentoEnviado = false;
                    flagfaturamentoBemAutoSemInteresse = false;
                    flagfaturamentoBemAutoPrazoExpirado = false;
                    flagfaturamentoBemAutoAnaliseGrcIniciada = false;
                    flagfaturamentoBemAutoPagamentoIniciado = false;
                    flagfaturamentoBemAutoAguardandoDetran = false;
                    flagfaturamentoBemAutoAguardandoCliente = false;
                    flagfaturamentoBemAutoAguardandoVistoria = false;
                    flagfaturamentoBemAutoAguardandoVeiculo = false;
                    flagfaturamentoBemAutoAutenticacaoIniciada = false;
                    flagfaturamentoBemAutoAutenticacaoReanaliseIniciada = false;
                    flagfaturamentoBemAutoVistoriaAgendada = false;
                    flagfaturamentoBemAutoVistoriaErro = false;
                    flagfaturamentoBemAutoVeiculoRecusado = false;
                    flagfaturamentoBemAutoGarantiaAprovada = false;
                    flagfaturamentoBemAutoIndicacaoErro = false;
                    flagfaturamentoBemAutoDadosCadastrados = false;
                    flagfaturamentoBemAutoAnaliseCanalIniciada = false;
                    flagfaturamentoBemAutoSaldoInsuficiente = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_ANALISE_GRC_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAnaliseGrcIniciada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAnaliseGrcIniciada = true;
                    flagfaturamentoBemAutoAguardandoDocumento = false;
                }

            } else if (FATURAMENTO_BEM_AUTO_AGUARDANDO_DETRAN.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAguardandoDetran) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAguardandoDetran = true;

                }

            } else if (FATURAMENTO_BEM_AUTO_AGUARDANDO_CLIENTE.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAguardandoCliente) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAguardandoCliente = true;

                }

            } else if (FATURAMENTO_BEM_AUTO_AGUARDANDO_VISTORIA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAguardandoVistoria) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAguardandoVistoria = true;
                    flagfaturamentoBemAutoAguardandoEscolhaBem = false;

                }

            } else if (FATURAMENTO_BEM_AUTO_AGUARDANDO_VEICULO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAguardandoVeiculo) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAguardandoVeiculo = true;
                    flagfaturamentoBemAutoAguardandoEscolhaBem = false;

                }

            } else if (FATURAMENTO_BEM_AUTO_BEM_ENCONTRADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoBemEncontrado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoBemEncontrado = true;
                    flagfaturamentoBemAutoAguardandoVistoria = false;

                }

            } else if (FATURAMENTO_BEM_AUTO_AUTENTICACAO_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAutenticacaoIniciada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAutenticacaoIniciada = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_AUTENTICACAO_REANALISE_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAutenticacaoReanaliseIniciada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAutenticacaoReanaliseIniciada = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_VISTORIA_ERRO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoVistoriaErro) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoVistoriaErro = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_VISTORIA_AGENDADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoVistoriaAgendada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoVistoriaAgendada = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_VEICULO_RECUSADO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoVeiculoRecusado) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoVeiculoRecusado = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_GARANTIA_APROVADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoGarantiaAprovada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoGarantiaAprovada = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_INDICACAO_ERRO.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoIndicacaoErro) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoIndicacaoErro = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_DADOS_CADASTRADOS.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoDadosCadastrados) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoDadosCadastrados = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_ANALISE_CANAL_INICIADA.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoAnaliseCanalIniciada) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoAnaliseCanalIniciada = true;
                }

            } else if (FATURAMENTO_BEM_AUTO_SALDO_INSUFICIENTE.equalsIgnoreCase(cota.getStatus())) {

                EventoFaturamentoBemAutoDTO dto = new EventoFaturamentoBemAutoDTO(
                        null, cota.getStatus(), cota.getDataEvento(), cota.getId(), cota.getIdCaso(), cota.getCaso(),
                        cota.getFase(), cota.getDsStatus(), cota.getObservacao(), cota.getTipoProcesso());

                if (!flagfaturamentoBemAutoSaldoInsuficiente) {
                    pilha.push(dto);
                    flagfaturamentoBemAutoSaldoInsuficiente = true;
                }

            } else {
                // Adicionar como evento padrao
                pilha.push(new EventoDTO(null, cota.getStatus(), cota.getDataEvento(), cota.getId()));
            }

        }

        // Percorrer a pilha e inserir na timeline
        // Antes de inserir na timeline aplicar filtrar de eventos
        ListIterator<EventoDTO> ListIterator = pilha.listIterator(pilha.size());
        List<EventoDTO> timeline = new ArrayList<>();

        // Se filtro nao informado, inserir apenas os eventos default
        if (eventosFiltro == null) {
            while (ListIterator.hasPrevious()) {
                EventoDTO evento = ListIterator.previous();
                if (EVENTOS_DEFAULT.contains(evento.getEvento())) {
                    timeline.add(evento);
                }
            }
        }

        // Se filtro de todos, inserir todos na timeline
        final String eventosFiltroDecoded = Util.base64Decode(eventosFiltro);
        if (eventosFiltroDecoded != null && eventosFiltroDecoded.contains("todos")) {
            while (ListIterator.hasPrevious()) {
                EventoDTO evento = ListIterator.previous();
                timeline.add(evento);
            }
        }

        // Se filtro especifico, inserir na timeline somente os eventos requisitados
        if (eventosFiltroDecoded != null) {
            while (ListIterator.hasPrevious()) {
                EventoDTO evento = ListIterator.previous();

                // Filtro com nome completo
                if (eventosFiltroDecoded.contains(evento.getEvento())) {
                    timeline.add(evento);
                    continue;
                }

                // Filtro com nome prefixado
                if (eventosFiltroDecoded.contains(ANALISE_CREDITO_MANUAL_)
                        && evento.getEvento().startsWith(ANALISE_CREDITO_MANUAL_)) {
                    timeline.add(evento);
                    continue;
                }

                if (eventosFiltroDecoded.contains(ANALISE_CREDITO_AUTOMATICA_)
                        && evento.getEvento().startsWith(ANALISE_CREDITO_AUTOMATICA_)) {
                    timeline.add(evento);
                    continue;
                }

                if (eventosFiltroDecoded.contains(AVISO_) && evento.getEvento().startsWith(AVISO_)) {
                    timeline.add(evento);
                    continue;
                }

                if (eventosFiltroDecoded.contains(COTA_) && evento.getEvento().startsWith(COTA_)) {
                    timeline.add(evento);
                    continue;
                }

                if (eventosFiltroDecoded.contains(FATURAMENTO_BEM_AUTO_)
                        && evento.getEvento().startsWith(FATURAMENTO_BEM_AUTO_)) {
                    timeline.add(evento);
                    continue;
                }

                if (eventosFiltroDecoded.contains(PAGAMENTO_) && evento.getEvento().startsWith(PAGAMENTO_)) {
                    timeline.add(evento);
                    continue;
                }

                if (eventosFiltroDecoded.contains(PROPOSTA_) && evento.getEvento().startsWith(PROPOSTA_)) {
                    timeline.add(evento);
                    continue;
                }

                // Filtro com agrupamentos
                if (eventosFiltroDecoded.contains(FATURAMENTO_BEM_AUTO_AGRUPADO_POR_CASO)
                        && evento.getEvento().startsWith(FATURAMENTO_BEM_AUTO_)) {
                    timeline.add(evento);
                    continue;
                }
            }

            // Agrupar os eventos mostrar apenas o ultimo
            if (eventosFiltroDecoded.contains(FATURAMENTO_BEM_AUTO_AGRUPADO_POR_CASO)) {
                Map<String, EventoDTO> faturamentoBemAutoAgrupadoMap = new HashMap<>();
                for (int i = 0; i < timeline.size(); i++) {
                    EventoDTO eventoDTO = timeline.get(i);

                    if (!eventoDTO.getEvento().startsWith(FATURAMENTO_BEM_AUTO_)) {
                        continue;
                    }

                    if (faturamentoBemAutoAgrupadoMap.containsKey(eventoDTO.getIdCaso())) {
                        EventoDTO eventoAgrupadoDTO = faturamentoBemAutoAgrupadoMap.get(eventoDTO.getIdCaso());
                        eventoAgrupadoDTO.setFlagExibirMaisEventos(true);
                        timeline.remove(i);
                        i--;
                        if (eventoDTO.getIdCaso().equals(idCasoFaturamentoBemAuto)) {
                            eventoAgrupadoDTO.setFlagExibirMaisEventos(false);
                            if (eventoAgrupadoDTO.getEventos() == null) {
                                eventoAgrupadoDTO.setEventos(new ArrayList<>());
                            }
                            eventoAgrupadoDTO.getEventos().add(eventoDTO);
                        }
                    }

                    if (!faturamentoBemAutoAgrupadoMap.containsKey(eventoDTO.getIdCaso())) {
                        faturamentoBemAutoAgrupadoMap.put(eventoDTO.getIdCaso(), eventoDTO);
                    }
                }
            }
        }

        // Corrigir os ids dos eventos de modo que fiquem ordenados do ultimo para o
        // primeiro
        for (int i = 0; i < timeline.size(); i++) {
            timeline.get(i).setId(timeline.size() - i);
            if (timeline.get(i).getEventos() != null) {
                for (int j = 0; j < timeline.get(i).getEventos().size(); j++) {
                    timeline.get(i).getEventos().get(j).setId(timeline.get(i).getEventos().size() - j);
                }
            }
        }

        // Obter o ultimo evento que é na verdade a ultima posicao/atualizacao de status
        // da cota
        CotaTimelineDTO dto = new CotaTimelineDTO();
        if (Objects.isNull(dto.getCota()) && !timeline.isEmpty()) {
            EventoDTO timelineEventoDTO = null;
            for (int i = 0; i < timeline.size(); i++) {
                timelineEventoDTO = timeline.get(i);
                if (EVENTOS_DEFAULT.contains(timelineEventoDTO.getEvento())) {
                    timelineEventoDTO = timeline.get(i);
                    break;
                }
            }
            if (timelineEventoDTO == null) {
                throw new NotFoundInfoException("Timeline inconsistente");
            }
            CotaEvento cotaEvento = null;
            for (CotaEvento e : eventosNaoDuplicadosOrdenados) {
                if (e.getId().equals(timelineEventoDTO.getIdEvento())) {
                    cotaEvento = e;
                    break;
                }
            }
            dto.setCota(new CotaEventoDTO(cotaEvento));
        }

        // Se nao localizou ultimo evento, ja interrompe e devolve para o controller
        if (Objects.isNull(dto.getCota())) {
            throw new NotFoundInfoException("Cota nao localizada");
        }

        // Inserir timeline
        dto.setTimeline(timeline);

        return dto;
    }

    /**
     * Order a busca no repositorio por DT_EVENTO ASC
     * 
     * @param limit
     * @param offset
     * @return
     */
    private Pageable getPageableAsc(String limit, String offset) {
        return PageRequest.of(Integer.parseInt(offset) - 1, Integer.parseInt(limit),
                Sort.by(
                        Sort.Order.asc("DT_EVENTO")));
    }

    /*
     * Percorre uma lista de eventos e localiza a presença de eventos a partir de um
     * indexIni ate um indexFim
     */
    private boolean temEventosInclusivo(Integer indexIni, Integer indexFim, List<CotaEvento> cotas, String... eventos) {
        if (indexFim > cotas.size() - 1)
            return false;
        if (indexFim == -1)
            indexFim = cotas.size();
        for (int i = indexIni; i < indexFim; i++) {
            for (String evento : eventos) {

                if (evento.equalsIgnoreCase(cotas.get(i).getStatus())) {
                    return true;
                }

                // Limitar o alcançe da busca
                // Se chegou ate a proxima contemplacao e nao encontrou, deverá sair para nao
                // conflitar com os eventos da contemplacao posterior
                if (evento.equalsIgnoreCase(COTA_CONTEMPLADA_LANCE)
                        || evento.equalsIgnoreCase(COTA_CONTEMPLADA_SORTEIO)) {
                    return false;
                }
            }
        }
        return false;
    }

    /*
     * Percorre uma lista de eventos e localiza tenta localizar o index do primeiro
     * evento detectado
     * de index ate fim da lista (de frente pra fim)
     */
    private Integer getProximoIndex(Integer index, List<CotaEvento> cotas, String... eventos) {
        for (int i = index; i < cotas.size(); i++) {
            for (String evento : eventos) {
                if (evento.equalsIgnoreCase(cotas.get(i).getStatus())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public Cota getCotaByIdCota(String idCota) {
        List<Cota> list = cotaRepository.findFirstCota(idCota);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
