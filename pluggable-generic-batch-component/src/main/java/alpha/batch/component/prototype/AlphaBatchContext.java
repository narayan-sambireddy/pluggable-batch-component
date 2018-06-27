package alpha.batch.component.prototype;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;


/**
 * @author narayana
 */
@Configuration
@EnableBatchProcessing
public class AlphaBatchContext {

	private JobBuilderFactory jobs;
	private StepBuilderFactory steps;

	public AlphaBatchContext(JobBuilderFactory jobs, StepBuilderFactory steps) {
		this.jobs = jobs;
		this.steps = steps;
	}

	@Bean
	public Job endOfDayBatch(Step prepareFile, Step transferFile) {
		return jobs
				.get("endOdDayBatch")
					.start(prepareFile)
					.next(transferFile)
				.build();
	}

	@Bean
	@JobScope
	public <S, D> Step prepareFile(ItemReader<S> alphaReader, ItemWriter<D> alphaWriter) {
		return steps
				.get("prepareFile")
					.<S, D>chunk(5).reader(alphaReader).writer(alphaWriter)
				.build();
	}

	@Bean
	@JobScope
	public Step transferFile() {
		return steps
				.get("transferFile")
					.tasklet((sc, cc) -> {
						System.out.println("---------- transferring file ------------");
						return RepeatStatus.FINISHED;
					}).build();
	}

	@Bean
	@StepScope
	public <S> JdbcCursorItemReader<S> alphaReader(
			RowMapper<S> alphaRowMapper,
			@Value("${alpha.batch.read.query}") String query,
			@Value("${alpha.batch.read.query.fetch-size}") Integer fetchSize){
		JdbcCursorItemReader<S> alphaReader = new JdbcCursorItemReader<>();
		alphaReader.setSql(query);
		alphaReader.setFetchSize(fetchSize);
		alphaReader.setRowMapper(alphaRowMapper);
		return alphaReader;
	}

	@Bean
	@StepScope
	public <D> FlatFileItemWriter<D> alphaWriter(
			@Value("${alpha.batch.write.file.path}") String filePath,
			@Value("${alpha.batch.write.file.delimiter}") String delimiter,
			@Value("${alpha.batch.write.file.field-names}") String[] fieldNames) {
		FlatFileItemWriter<D> alphaWriter = new FlatFileItemWriter<>();
		alphaWriter.setResource(new FileSystemResource(filePath + "_" + System.currentTimeMillis() + ".txt"));
		alphaWriter.setAppendAllowed(true);
		alphaWriter.setLineAggregator(new DelimitedLineAggregator<D>() {{
			setDelimiter(delimiter);
			setFieldExtractor(new BeanWrapperFieldExtractor() {{
				setNames(fieldNames);
			}});
		}});
		return alphaWriter;
	}

}
