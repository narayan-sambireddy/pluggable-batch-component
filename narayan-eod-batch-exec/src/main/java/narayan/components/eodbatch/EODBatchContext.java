package narayan.components.eodbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import static narayan.components.eodbatch.EODBatchUtil.*;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

/**
 * Generic batch component --
 * 			to prepare & transfer the data extracted from a DB to a remote server / endpoint
 *
 * @author narayana
 *
 */
class EODBatchContext {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EODBatchProperties props;

    public EODBatchContext(JobBuilderFactory jobs, StepBuilderFactory steps, EODBatchProperties props) {
        this.jobs = jobs;
        this.steps = steps;
        this.props = props;
    }

    @Bean
    public Job eodJob(Step prepareFile, Step transferFile) {
        return jobs
                .get("eodJob")
                    .start(prepareFile)
                    .next(transferFile)
                .preventRestart()
                .build();
    }

    @Bean
    @JobScope
    public <S,D> Step prepareFile(ItemReader<S> alphaReader, ItemWriter<D> alphaWriter) {
        return steps
                .get("prepareFile")
                    .<S,D>chunk(props.getCommitInterval())
                        .reader(alphaReader)
                        .writer(alphaWriter)
                    .build();
    }

    @Bean
    @JobScope
    public <S, D> Step transferFile() {
        return steps.get("transferFile").tasklet((sc, ck) -> {
            System.out.println("Hello");
            return FINISHED;
        }).build();
    }

    @Bean
    @StepScope
    public <S> JdbcCursorItemReader<S> alphaReader(
            DataSource dataSource, RowMapper<S> alphaRowMapper, PreparedStatementSetter alphaPSS) {
        return new JdbcCursorItemReader<S>() {{
            setDataSource(dataSource);
            setRowMapper(alphaRowMapper);
            setSql(props.getSql());
            setPreparedStatementSetter(alphaPSS);
            setFetchSize(props.getFetchSize());
        }};
    }


    @Bean
    @StepScope
    public <D> FlatFileItemWriter<D> alphaWriter(@Value("#{stepExecution}") final StepExecution context) {
        final String nowInISO = nowInISO();
        final String fileName = getFileName(props.getFileNamePrefix(), nowInISO);
        final String fileAbsolutePath = getFileAbsolutePath(props.getFilePath(), fileName);
        return new FlatFileItemWriter<D>() {{
            setHeaderCallback((writer) -> writer.write(prepareHeader(props.getDelimiter(), props.getHeaderMarker(), fileName, nowInISO)));
            setFooterCallback((writer) -> writer.write(prepareFooter(props.getDelimiter(), props.getFooterMarker(), context.getWriteCount())));
            setAppendAllowed(false);
            setResource(new FileSystemResource(fileAbsolutePath));
            setLineAggregator(new DelimitedLineAggregator<D>() {{
                setDelimiter(props.getDelimiter());
                setFieldExtractor(new BeanWrapperFieldExtractor<D>() {{
                    setNames(props.getFieldsList());
                }});
            }});
        }};
    }


}
