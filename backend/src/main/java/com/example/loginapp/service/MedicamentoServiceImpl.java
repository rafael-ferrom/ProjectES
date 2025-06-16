package com.example.loginapp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.loginapp.dto.CompraDTO;
import com.example.loginapp.dto.MedicamentoDTO;
import com.example.loginapp.entity.Bula;
import com.example.loginapp.entity.Dose;
import com.example.loginapp.entity.EstoqueMedicamento;
import com.example.loginapp.entity.EstoqueStatus;
import com.example.loginapp.entity.Frequencia;
import com.example.loginapp.entity.Medicamento;
import com.example.loginapp.entity.User;
import com.example.loginapp.repository.BulaRepository;
import com.example.loginapp.repository.DoseRepository;
import com.example.loginapp.repository.EstoqueRepository;
import com.example.loginapp.repository.MedicamentoRepository;
import com.example.loginapp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

  private final MedicamentoRepository medicamentoRepository;
  private final UserRepository userRepository;
  private final BulaRepository bulaRepository;
  private final DoseRepository doseRepository;
  // >>>>> ALTERAÇÃO AQUI <<<<<
  private final EstoqueRepository estoqueRepository;

  @Autowired
  public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository, UserRepository userRepository,
      BulaRepository bulaRepository, DoseRepository doseRepository, EstoqueRepository estoqueRepository) {
    this.medicamentoRepository = medicamentoRepository;
    this.userRepository = userRepository;
    this.bulaRepository = bulaRepository;
    this.doseRepository = doseRepository;
    // >>>>> ALTERAÇÃO AQUI <<<<<
    this.estoqueRepository = estoqueRepository;
  }

  @Override
  @Transactional
  public Medicamento adicionarMedicamento(MedicamentoDTO dto) {
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    Bula bula = bulaRepository.findById(dto.getBulaId())
        .orElseThrow(() -> new RuntimeException("Bula não encontrada."));

    Frequencia frequencia = new Frequencia(dto.getDataInicio(), dto.getDataTermino(), dto.getVezesPorDia());

    Medicamento medicamento = new Medicamento(dto.getNome(), dto.getDosagem(), frequencia, bula, dto.getTipo(),
        user);

    if (dto.getInstrucoes() != null && !dto.getInstrucoes().isEmpty()) {
      for (String instrucaoTexto : dto.getInstrucoes()) {
        medicamento.adicionarInstrucao(instrucaoTexto);
      }
    }

    return medicamentoRepository.save(medicamento);
  }

  @Override
  public List<Medicamento> listarPorUsuario(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    return medicamentoRepository.findByUser(user);
  }

  @Override
  public Optional<Medicamento> buscarPorId(Long id) {
    return medicamentoRepository.findById(id);
  }

  @Override
  @Transactional
  public void registrarDose(Long medicamentoId) {
    // 1. Encontrar o plano de tratamento (Medicamento)
    Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
        .orElseThrow(() -> new RuntimeException("Plano de tratamento não encontrado."));

    User user = medicamento.getUser();
    Bula bula = medicamento.getBula();

    // 2. Encontrar uma caixa de remédio disponível (primeiro que vence)
    Sort sortByValidade = Sort.by(Sort.Direction.ASC, "dataValidade");
    List<EstoqueMedicamento> estoquesDisponiveis = estoqueRepository.findByUserAndBulaAndStatusAndDataValidadeAfter(
      user, bula, EstoqueStatus.DISPONIVEL, LocalDate.now().minusDays(1), sortByValidade);

    if (estoquesDisponiveis.isEmpty()) {
      throw new RuntimeException("Sem estoque disponível ou todas as caixas estão vencidas.");
    }

    EstoqueMedicamento caixaAtual = estoquesDisponiveis.get(0);

    // 3. Abater a dose e atualizar o estoque
    int comprimidosRestantes = caixaAtual.getQuantidadeComprimidos();
    if (comprimidosRestantes <= 0) {
      // Isso não deveria acontecer com a query atual, mas é uma boa verificação
      throw new RuntimeException("Erro de consistência: Caixa marcada como disponível está vazia.");
    }

    caixaAtual.setQuantidadeComprimidos(comprimidosRestantes - 1);

    if (caixaAtual.getQuantidadeComprimidos() == 0) {
      caixaAtual.setStatus(EstoqueStatus.VAZIO);
    }
    estoqueRepository.save(caixaAtual);
    
    // 4. Registrar a dose, vinculando ao plano e à caixa
    Dose novaDose = new Dose(medicamento, caixaAtual);
    doseRepository.save(novaDose);
  }

  // >>>>> NOVO MÉTODO <<<<<
  @Override
  @Transactional
  public List<EstoqueMedicamento> comprarMedicamento(CompraDTO compraDTO) {
      User user = userRepository.findById(compraDTO.getUserId())
              .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
      Bula bula = bulaRepository.findById(compraDTO.getBulaId())
              .orElseThrow(() -> new RuntimeException("Bula não encontrada."));

      List<EstoqueMedicamento> caixasCompradas = new ArrayList<>();
      LocalDate hoje = LocalDate.now();
      LocalDate dataValidade = hoje.plusYears(compraDTO.getValidadeEmAnos());

      for (int i = 0; i < compraDTO.getNumeroDeCaixas(); i++) {
          EstoqueMedicamento novaCaixa = new EstoqueMedicamento(
                  user,
                  bula,
                  hoje,
                  dataValidade,
                  compraDTO.getComprimidosPorCaixa()
          );
          caixasCompradas.add(novaCaixa);
      }

      return estoqueRepository.saveAll(caixasCompradas);
  }
    
  // >>>>> NOVO MÉTODO <<<<<
  @Override
  public List<EstoqueMedicamento> listarEstoquePorUsuario(Long userId) {
      User user = userRepository.findById(userId)
              .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
      return estoqueRepository.findByUser(user);
  }
}