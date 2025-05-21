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
@Profile("test")
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

		User u1 = new User("Maria Silva", "maria@gmail.com", "123456");
		User u2 = new User("João Santos", "joao@gmail.com", "123456");
		User u3 = new User("Rafael", "rafael@gmail.com", "123456");
		User u4 = new User("Lucas", "lucas@gmail.com", "123456");
		User u5 = new User("Deivid", "deivid@gmail.com", "123456");
		User u6 = new User("Diego", "diego@gmail.com", "123456");

		userRepository.saveAll(Arrays.asList(u1, u2, u3, u4, u5, u6));

		Bula b1 = new Bula("Dipirona", "Dipirona sódica", "500mg", "Comprimido", "Caixa com 20 comprimidos", "Medley");

		Bula b2 = new Bula("Paracetamol", "Paracetamol", "750mg", "Comprimido", "Caixa com 20 comprimidos", "EMS");

		Bula b3 = new Bula("Amoxicilina", "Amoxicilina tri-hidratada", "500mg", "Cápsula", "Caixa com 15 cápsulas",
				"Neo Química");

		Bula b4 = new Bula("Loratadina", "Loratadina", "10mg", "Comprimido", "Caixa com 12 comprimidos", "Medley");

		Bula b5 = new Bula("Profenid", "Cetoprofeno", "100mg", "Cápsula", "Caixa com 12 cápsulas", "Sanofi Aventis");

		Bula b6 = new Bula("Minoxilina", "Minociclina", "100mg", "Cápsula", "Caixa com 20 cápsulas", "Aché");

		Bula b7 = new Bula("Buscopan", "Butilbrometo de escopolamina", "10mg", "Comprimido", "Caixa com 20 comprimidos",
				"Boehringer Ingelheim");

		Bula b8 = new Bula("Desalex", "Desloratadina", "5mg", "Comprimido", "Caixa com 10 comprimidos", "MSD");

		LocalDate hoje = LocalDate.now();

		Frequencia f1 = new Frequencia(hoje, hoje.plusDays(5), 3);
		Frequencia f2 = new Frequencia(hoje, hoje.plusDays(3), 4);
		Frequencia f3 = new Frequencia(hoje, hoje.plusDays(7), 3);
		Frequencia f4 = new Frequencia(hoje, hoje.plusDays(15), 1);
		Frequencia f5 = new Frequencia(hoje, hoje.plusDays(10), 2);
		Frequencia f6 = new Frequencia(hoje, hoje.plusDays(14), 1);
		Frequencia f7 = new Frequencia(hoje, hoje.plusDays(7), 3);
		Frequencia f8 = new Frequencia(hoje, hoje.plusDays(10), 1);

		Medicamento m1 = new Medicamento("Dipirona", "500mg", f1, b1, "comprimido", u1);
		Medicamento m2 = new Medicamento("Paracetamol", "750mg", f2, b2, "comprimido", u1);
		Medicamento m3 = new Medicamento("Amoxicilina", "500mg", f3, b3, "cápsula", u2);
		Medicamento m4 = new Medicamento("Loratadina", "10mg", f4, b4, "comprimido", u2);
		Medicamento m5 = new Medicamento("Profenid", "100mg", f5, b5, "cápsula", u3);
		Medicamento m6 = new Medicamento("Minoxilina", "100mg", f6, b6, "cápsula", u4);
		Medicamento m7 = new Medicamento("Buscopan", "10mg", f7, b7, "comprimido", u5);
		Medicamento m8 = new Medicamento("Desalex", "5mg", f8, b8, "comprimido", u6);

		m1.adicionarInstrucao("Tomar 1 comprimido a cada 8 horas");
		m1.adicionarInstrucao("Tomar após as refeições");

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

		medicamentoRepository.saveAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8));
	}
}