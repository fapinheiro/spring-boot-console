package labs.pinheiro.springbootconsole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import labs.pinheiro.springbootconsole.arearestrita.AreaRestritaHelper;
import labs.pinheiro.springbootconsole.arearestrita.service.impl.TimelineServiceImpl;


@SpringBootApplication
@EnableAutoConfiguration
public class SpringBootConsoleApplication implements CommandLineRunner {

	@Autowired
	private AreaRestritaHelper areaRestritaHelper;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootConsoleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		areaRestritaHelper.gerarCotasComUltimoEventoTimelinePorArquivo("payload.txt",  TimelineServiceImpl.COTA_DESCLASSIFICADA, false);

	}

	
	

}
