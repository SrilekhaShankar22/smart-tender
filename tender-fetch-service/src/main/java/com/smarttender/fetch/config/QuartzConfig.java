package com.smarttender.fetch.config;
import com.smarttender.fetch.job.TenderFetchJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration @RequiredArgsConstructor
public class QuartzConfig {
    private final AppProperties props;
    @Bean
    public JobDetail tenderFetchJobDetail() {
        return JobBuilder.newJob(TenderFetchJob.class)
                .withIdentity("tenderFetchJob", "tenderGroup")
                .storeDurably(true).build();
    }
    @Bean
    public Trigger tenderFetchTrigger(JobDetail tenderFetchJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(tenderFetchJobDetail)
                .withIdentity("tenderFetchTrigger", "tenderGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(props.getScheduler().getFetchCron())
                        .withMisfireHandlingInstructionDoNothing())
                .build();
    }
}
