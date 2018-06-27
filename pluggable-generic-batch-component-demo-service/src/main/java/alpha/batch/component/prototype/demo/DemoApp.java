package alpha.batch.component.prototype.demo;

import alpha.batch.component.prototype.AlphaBatchContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author narayana
 */
@SpringBootApplication
@Import({AlphaBatchContext.class})
public class DemoApp {

	public static void main(String[] args) {
		SpringApplication.run(DemoApp.class, args);
	}
}
