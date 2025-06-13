package com.example.loginapp.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;

import com.example.loginapp.entity.Bula;
import com.example.loginapp.entity.Frequencia;
import com.example.loginapp.entity.Medicamento;
import com.example.loginapp.entity.User;
import com.example.loginapp.repository.BulaRepository;
import com.example.loginapp.repository.FrequenciaRepository;
import com.example.loginapp.repository.MedicamentoRepository;
import com.example.loginapp.repository.UserRepository;

// @Configuration
// @Profile("test")
public class TestConfig implements CommandLineRunner {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MedicamentoRepository medicamentoRepository;

  @Autowired
  private BulaRepository bulaRepository;

  // O FrequenciaRepository é necessário porque Frequencia também é salva em cascata
  // a partir de Medicamento, então não precisa ser injetado se não for usado diretamente.
  @Autowired
  private FrequenciaRepository frequenciaRepository;

  @Override
  public void run(String... args) throws Exception {

    // 1. Salva os Usuários
    User u1 = new User("Maria Silva", "maria@gmail.com", "123456");
    User u2 = new User("João Santos", "joao@gmail.com", "123456");
    User u3 = new User("Rafael", "rafael@gmail.com", "123456");
    User u4 = new User("Lucas", "lucas@gmail.com", "123456");
    User u5 = new User("Deivid", "deivid@gmail.com", "123456");
    User u6 = new User("Diego", "diego@gmail.com", "123456");

    userRepository.saveAll(Arrays.asList(u1, u2, u3, u4, u5, u6));

    // 2. Cria as instâncias de Bula
    Bula b1 = new Bula("Dipirona", "Dipirona sódica", "500mg", "Comprimido", "Caixa com 20 comprimidos", "Prati", "https://paguemenos.vtexassets.com/arquivos/ids/644482-800-auto?v=638008072496630000&width=800&height=auto&aspect=true");
    b1.adicionarInstrucao("Tomar com um copo de água.");
    b1.adicionarInstrucao("Não exceder a dose recomendada.");

    Bula b2 = new Bula("Paracetamol", "Paracetamol", "750mg", "Comprimido", "Caixa com 20 comprimidos", "neo química", "https://paguemenos.vtexassets.com/arquivos/ids/641086/paracetamol-750mg-com-20-comprimidos-generico-neo-quimica-principal.jpg?v=638008025296600000");
    b2.adicionarInstrucao("Pode ser tomado com ou sem alimentos.");
    b2.adicionarInstrucao("Em caso de febre persistente, procure um médico.");

    Bula b3 = new Bula("Amoxicilina", "Amoxicilina tri-hidratada", "500mg", "Cápsula", "Caixa com 15 cápsulas", "Teuto", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSWONGwEMA34vZzdyKEuEQPCW9C5kIC31BXbw&s");
    b3.adicionarInstrucao("Completar o ciclo de tratamento, mesmo com a melhora dos sintomas.");
    b3.adicionarInstrucao("Tomar em intervalos regulares.");

    Bula b4 = new Bula("Loratadina", "Loratadina", "10mg", "Comprimido", "Caixa com 12 comprimidos", "neo química", "https://cdn1.staticpanvel.com.br/produtos/15/157190-15.jpg");
    b4.adicionarInstrucao("Tomar uma vez ao dia.");

    Bula b5 = new Bula("Profenid", "Cetoprofeno", "100mg", "Cápsula", "Caixa com 12 cápsulas", "Sanofi Aventis", "https://www.santoremedio.com.br/media/produtos/7896070601765_400x400_qmTGXyz.png");
    b5.adicionarInstrucao("Tomar junto com as refeições para evitar desconforto gástrico.");

    Bula b6 = new Bula("Minoxilina", "Minociclina", "100mg", "Cápsula", "Caixa com 20 cápsulas", "Aché", "https://vipfarma.com.br/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/a/c/ache.jpg");
    b6.adicionarInstrucao("Evitar exposição solar excessiva durante o tratamento.");
    
    Bula b7 = new Bula("Buscopan", "Butilbrometo de escopolamina", "10mg", "Comprimido", "Caixa com 20 comprimidos", "Boehringer Ingelheim", "https://www.drogarianovaesperanca.com.br/imagens/600x600/buscopan-10mg-com-20-drageas-edd4e874ab.jpg");
    b7.adicionarInstrucao("Indicado para cólicas e dores abdominais.");
    
    Bula b8 = new Bula("Desalex", "Desloratadina", "5mg", "Comprimido", "Caixa com 10 comprimidos", "MSD", "https://www.drogarianovaesperanca.com.br/imagens/600x600/desalex-05mg-com-100ml-2e6b0861c8.jpg");
    b8.adicionarInstrucao("Não causa sonolência na maioria dos pacientes.");

    // >>>>> PASSO CRUCIAL: SALVAR AS BULAS PRIMEIRO <<<<<
    // Esta linha precisa existir aqui, ANTES de criar os Medicamentos.
    bulaRepository.saveAll(Arrays.asList(b1, b2, b3, b4, b5, b6, b7, b8));
    
    // 3. Cria as Frequencias (elas serão salvas em cascata a partir do Medicamento)
    LocalDate hoje = LocalDate.now();
    Frequencia f1 = new Frequencia(hoje, hoje.plusDays(5), 3);
    Frequencia f2 = new Frequencia(hoje, hoje.plusDays(3), 4);
    Frequencia f3 = new Frequencia(hoje, hoje.plusDays(7), 3);
    Frequencia f4 = new Frequencia(hoje, hoje.plusDays(15), 1);
    Frequencia f5 = new Frequencia(hoje, hoje.plusDays(10), 2);
    Frequencia f6 = new Frequencia(hoje, hoje.plusDays(14), 1);
    Frequencia f7 = new Frequencia(hoje, hoje.plusDays(7), 3);
    Frequencia f8 = new Frequencia(hoje, hoje.plusDays(10), 1);

    // 4. Cria os Medicamentos, agora associando com as Bulas que JÁ EXISTEM no banco.
    Medicamento m1 = new Medicamento("Dipirona", "500mg", f1, b1, "comprimido", u1);
    Medicamento m2 = new Medicamento("Paracetamol", "750mg", f2, b2, "comprimido", u1);
    Medicamento m3 = new Medicamento("Amoxicilina", "500mg", f3, b3, "cápsula", u2);
    Medicamento m4 = new Medicamento("Loratadina", "10mg", f4, b4, "comprimido", u2);
    Medicamento m5 = new Medicamento("Profenid", "100mg", f5, b5, "cápsula", u3);
    Medicamento m6 = new Medicamento("Minoxilina", "100mg", f6, b6, "cápsula", u4);
    Medicamento m7 = new Medicamento("Buscopan", "10mg", f7, b7, "comprimido", u5);
    Medicamento m8 = new Medicamento("Desalex", "5mg", f8, b8, "comprimido", u6);

    // Adiciona as instruções específicas do tratamento (diferentes das instruções da bula)
    m1.adicionarInstrucao("Tomar 1 comprimido a cada 8 horas");
    m1.adicionarInstrucao("Tomar após as refeições");
    // ... (restante das instruções para m2 a m8)
    m2.adicionarInstrucao("Tomar 1 comprimido a cada 6 horas");
    m2.adicionarInstrucao("Não exceder 4 comprimidos por dia");
    m3.adicionarInstrucao("Tomar 1 cápsula a cada 8 horas");
    m3.adicionarInstrucao("Tomar o tratamento completo, mesmo que os sintomas melhorem");
    m3.adicionarInstrucao("Tomar com um copo cheio de água");
    m4.adicionarInstrucao("Tomar 1 comprimido uma vez ao dia");
    m4.adicionarInstrucao("Pode ser tomado com ou sem alimentos");
    m5.adicionarInstrucao("Tomar 1 cápsula duas vezes ao dia");
    m5.adicionarInstrucao("Tomar com alimentos para evitar irritação gástrica");
    m6.adicionarInstrucao("Tomar 1 cápsula uma vez ao dia");
    m6.adicionarInstrucao("Evitar exposição ao sol durante o tratamento");
    m7.adicionarInstrucao("Tomar 1 comprimido três vezes ao dia");
    m7.adicionarInstrucao("Tomar com um copo de água");
    m8.adicionarInstrucao("Tomar 1 comprimido uma vez ao dia");
    m8.adicionarInstrucao("Preferencialmente no mesmo horário todos os dias");

    // 5. Salva os Medicamentos. A cascata salvará as Frequencias e Instrucoes, e a Bula já existirá.
    medicamentoRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8));
  }
}