package narayan.components.eodbatch.demo;

import narayan.components.eodbatch.EnableEODBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author narayana
 *
 */
@SpringBootApplication
@EnableEODBatchProcessing
public class PluggableBatchDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(PluggableBatchDemoApp.class, args);
	}
}
