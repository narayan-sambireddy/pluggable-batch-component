package narayan.components.eodbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

/**
 *
 * @author narayana
 *
 */
@Service
class EODBatchScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(EODBatchScheduler.class);

    private final Job eodJob;
    private final JobLauncher eodJobLauncher;

    @Value("${batch.job.params}")
    private Properties jobParams;

    public EODBatchScheduler(Job eodJob, JobLauncher eodJobLauncher) {
        this.eodJob = eodJob;
        this.eodJobLauncher = eodJobLauncher;
    }

    @Scheduled(cron = "${batch.scheduler.cron}")
    public void launchEodJob() throws Exception {
        JobParametersBuilder paramBuilder = new JobParametersBuilder(jobParams);
        paramBuilder.addDate("RUN-TIME", new Date());
        JobExecution jobExecution = eodJobLauncher.run(eodJob, paramBuilder.toJobParameters());
        LOG.info("Job ID : {}", jobExecution.getJobId());
        LOG.info("Job PARAMS : {}", jobExecution.getJobParameters());
    }

}
