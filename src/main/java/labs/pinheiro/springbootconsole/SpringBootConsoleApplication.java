package labs.pinheiro.springbootconsole;

import java.util.List;

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

		// System.out.print(teste.replaceAll("\"DT_PROCESSADO\": {\"int\": [0-9]*}", ""));
		// areaRestritaHelper.gerarCotasComUltimoEventoTimelinePorArquivo("payload.txt",  List.of(TimelineServiceImpl.COTA_ALOCADA) , true);
		
		// areaRestritaHelper.gerarEventoUnico("payload.txt");

    	areaRestritaHelper.gerarPayloadDPL("payload.txt", TimelineServiceImpl.COTA_ALOCADA);


	}

	
	

}
