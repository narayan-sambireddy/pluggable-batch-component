package alpha.batch.component.prototype;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Properties;

/**
 * @author narayana
 */
@Service
class AlphaBatchScheduler {

    private JobLauncher jobLauncher;
    private Job endOfDayJob;

    @Value("#{${alpha.batch.job-params}}")
    private Properties jobParams;

    public AlphaBatchScheduler(JobLauncher jobLauncher, Job endOfDayJob) {
        this.jobLauncher = jobLauncher;
        this.endOfDayJob = endOfDayJob;
    }

    @Scheduled(cron = "${alpha.batch.job.scheduler.cron}")
    public void launchEODJob() throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder(jobParams);
        jobParametersBuilder.addDate("LAUNCH_TIME", new Date());
        jobLauncher.run(endOfDayJob, jobParametersBuilder.toJobParameters());
    }
}
