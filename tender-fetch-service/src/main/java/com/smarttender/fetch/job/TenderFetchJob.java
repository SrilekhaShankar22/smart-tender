package com.smarttender.fetch.job;
import com.smarttender.fetch.service.impl.TenderFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
@Slf4j @Component @DisallowConcurrentExecution @RequiredArgsConstructor
public class TenderFetchJob extends QuartzJobBean {
    private final TenderFetchService fetchService;
    @Override
    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        String jobId = ctx.getFireInstanceId();
        log.info("TenderFetchJob START id={}", jobId);
        try { fetchService.runFetchCycle(jobId); }
        catch (Exception e) { log.error("Job FAILED id={}: {}", jobId, e.getMessage()); throw new JobExecutionException(e, false); }
        log.info("TenderFetchJob FINISH id={}", jobId);
    }
}
