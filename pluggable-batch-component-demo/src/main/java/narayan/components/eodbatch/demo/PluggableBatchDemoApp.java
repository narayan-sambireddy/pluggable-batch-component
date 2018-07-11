package narayan.components.eodbatch.demo;

import narayan.components.eodbatch.EnableEODBatchProcessing;
import narayan.components.eodbatch.demo.domain.Alpha;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;

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

	@Bean(name = "alphaRowMapper")
	public RowMapper<Alpha> alphaRowMapper() {
		return (rs, rn) -> new Alpha() {{
			setId(rs.getInt("id"));
			setName(rs.getString("name"));
			setCity(rs.getString("city"));
			setCountry(rs.getString("country"));
		}};
	}

	@Bean(name="alphaPSS")
	public PreparedStatementSetter alphaPSS() {
		return (PreparedStatement ps) -> {
			ps.setInt(1, 2);
			ps.setInt(2, 4);
		};
	}
}
