package com.example.loginapp.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.loginapp.entity.Bula;
import com.example.loginapp.entity.Frequencia;
import com.example.loginapp.entity.Medicamento;
import com.example.loginapp.entity.User;
import com.example.loginapp.repository.BulaRepository;
import com.example.loginapp.repository.FrequenciaRepository;
import com.example.loginapp.repository.MedicamentoRepository;
import com.example.loginapp.repository.UserRepository;

@Configuration
@Profile("test") // Este perfil será ativado ao iniciar a aplicação com o perfil "test"
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    
    @Autowired
    private BulaRepository bulaRepository;
    
    @Autowired
    private FrequenciaRepository frequenciaRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Criação dos usuários
        User u1 = new User("Maria Silva", "maria@gmail.com", "123456");
        User u2 = new User("João Santos", "joao@gmail.com", "123456");
        
        // Salva os usuários para gerar os IDs
        userRepository.saveAll(Arrays.asList(u1, u2));
        
        // Criação de bulas
        Bula b1 = new Bula("Dipirona", "Dipirona sódica", "500mg", "Comprimido", 
                "Caixa com 20 comprimidos", "Medley");
        
        Bula b2 = new Bula("Paracetamol", "Paracetamol", "750mg", "Comprimido", 
                "Caixa com 20 comprimidos", "EMS");
        
        Bula b3 = new Bula("Amoxicilina", "Amoxicilina tri-hidratada", "500mg", "Cápsula", 
                "Caixa com 15 cápsulas", "Neo Química");
        
        Bula b4 = new Bula("Loratadina", "Loratadina", "10mg", "Comprimido", 
                "Caixa com 12 comprimidos", "Medley");
        
        // Criação de frequências
        LocalDate hoje = LocalDate.now();
        
        Frequencia f1 = new Frequencia(hoje, hoje.plusDays(5), 3);
        Frequencia f2 = new Frequencia(hoje, hoje.plusDays(3), 4);
        Frequencia f3 = new Frequencia(hoje, hoje.plusDays(7), 3);
        Frequencia f4 = new Frequencia(hoje, hoje.plusDays(15), 1);
        
        // Criação dos medicamentos
        Medicamento m1 = new Medicamento("Dipirona", "500mg", f1, b1, "comprimido", u1);
        Medicamento m2 = new Medicamento("Paracetamol", "750mg", f2, b2, "comprimido", u1);
        Medicamento m3 = new Medicamento("Amoxicilina", "500mg", f3, b3, "cápsula", u2);
        Medicamento m4 = new Medicamento("Loratadina", "10mg", f4, b4, "comprimido", u2);
        
        // Adiciona instruções aos medicamentos
        m1.adicionarInstrucao("Tomar 1 comprimido a cada 8 horas");
        m1.adicionarInstrucao("Tomar após as refeições");
        
        m2.adicionarInstrucao("Tomar 1 comprimido a cada 6 horas");
        m2.adicionarInstrucao("Não exceder 4 comprimidos por dia");
        
        m3.adicionarInstrucao("Tomar 1 cápsula a cada 8 horas");
        m3.adicionarInstrucao("Tomar o tratamento completo, mesmo que os sintomas melhorem");
        m3.adicionarInstrucao("Tomar com um copo cheio de água");
        
        m4.adicionarInstrucao("Tomar 1 comprimido uma vez ao dia");
        m4.adicionarInstrucao("Pode ser tomado com ou sem alimentos");
        
        // Salva os medicamentos (que irão salvar em cascata as bulas, frequências e instruções)
        medicamentoRepository.saveAll(Arrays.asList(m1, m2, m3, m4));
    }
}