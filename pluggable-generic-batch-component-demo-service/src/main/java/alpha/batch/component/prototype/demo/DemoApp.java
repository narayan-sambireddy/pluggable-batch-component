package alpha.batch.component.prototype.demo;

import alpha.batch.component.prototype.AlphaBatchContext;
import alpha.batch.component.prototype.demo.domain.Alpha;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author narayana
 */
@SpringBootApplication
@Import({AlphaBatchContext.class})
public class DemoApp {

	public static void main(String[] args) {
		SpringApplication.run(DemoApp.class, args);
	}


	@Bean
	public RowMapper<Alpha> alphaRowMapper() {
		return (rs, rn) -> new Alpha() {{
			setId(rs.getString("id"));
			setName(rs.getString("name"));
			setCity(rs.getString("city"));
		}};
	}
}
