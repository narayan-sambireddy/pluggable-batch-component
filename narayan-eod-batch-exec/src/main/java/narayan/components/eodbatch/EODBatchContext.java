package narayan.components.eodbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author narayana
 * 
 */
@Configuration
@EnableScheduling
@EnableBatchProcessing
public class EODBatchContext {

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
}
