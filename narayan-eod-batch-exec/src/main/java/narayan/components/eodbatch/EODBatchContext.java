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

    public EODBatchContext(JobBuilderFactory jobs, StepBuilderFactory steps) {
        this.jobs = jobs;
        this.steps = steps;
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
    public <S,D> Step prepareFile(ItemReader<S> alphaReader, ItemWriter<D> alphaWriter,
            @Value("${batch.chunk.commit-interval}") Integer commitInterval) {
        return steps
                .get("prepareFile")
                    .<S,D>chunk(commitInterval)
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
            DataSource dataSource, RowMapper<S> alphaRowMapper, PreparedStatementSetter alphaPSS,
            @Value("${fetch.data.sql}") String fetchDataSql, @Value("${fetch.data.size}") Integer fetchDataSize) {
        return new JdbcCursorItemReader<S>() {{
            setDataSource(dataSource);
            setRowMapper(alphaRowMapper);
            setSql(fetchDataSql);
            setPreparedStatementSetter(alphaPSS);
            setFetchSize(fetchDataSize);
        }};
    }


    @Bean
    @StepScope
    public <D> FlatFileItemWriter<D> alphaWriter(
            @Value("${target.file.path}") final String targetFilePath,
            @Value("${target.file.name.prefix}") final String targetFileNamePrefix,
            @Value("${batch.file.delimiter}") final String delimiter,
            @Value("${batch.file.fields.list}") final String[] batchFileFieldsList,
            @Value("${batch.file.header.marker}") final String headerMarker,
            @Value("${batch.file.footer.marker}") final String footerMarker,
            @Value("#{stepExecution}") final StepExecution context) {
        final String nowInISO = nowInISO();
        final String fileName = getFileName(targetFileNamePrefix, nowInISO);
        final String fileAbsolutePath = getFileAbsolutePath(targetFilePath, fileName);
        return new FlatFileItemWriter<D>() {{
            setHeaderCallback((writer) -> writer.write(prepareHeader(delimiter, headerMarker, fileName, nowInISO)));
            setFooterCallback((writer) -> writer.write(prepareFooter(delimiter, footerMarker, context.getWriteCount())));
            setAppendAllowed(false);
            setResource(new FileSystemResource(fileAbsolutePath));
            setLineAggregator(new DelimitedLineAggregator<D>() {{
                setDelimiter(delimiter);
                setFieldExtractor(new BeanWrapperFieldExtractor<D>() {{
                    setNames(batchFileFieldsList);
                }});
            }});
        }};
    }


}
