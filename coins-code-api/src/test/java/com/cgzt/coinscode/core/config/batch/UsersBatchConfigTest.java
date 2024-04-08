package com.cgzt.coinscode.core.config.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@Sql(value = "/sqls/clear-all-tables-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UsersBatchConfigTest {
    int EXPECTED_COUNT = 10;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${user.profile.dir}")
    String imageDir;

    @Test
    void testJob(@Autowired Job job) throws Exception {
        var file = new File(imageDir);

        this.jobLauncherTestUtils.setJob(job);

        var jobExecution = jobLauncherTestUtils.launchJob();


        var stepExecutions = jobExecution.getStepExecutions();
        var stepExecution = new ArrayList<>(stepExecutions.stream().toList()).get(0);
        var users_count = jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        var users_account_count = jdbcTemplate.queryForObject("select count(*) from user_account", Integer.class);

        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        assertEquals(EXPECTED_COUNT, stepExecution.getReadCount());
        assertEquals(EXPECTED_COUNT, stepExecution.getWriteCount());
        assertEquals(EXPECTED_COUNT, users_count);
        assertEquals(EXPECTED_COUNT, users_account_count);
        assertEquals(EXPECTED_COUNT, file.list().length);

        file.delete();
    }
}