package com.cgzt.coinscode.core.config.batch;

import com.cgzt.coinscode.users.adapters.outbound.batch.UserAccountProcessor;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
class UsersBatchConfig {
    static final String STEP_NAME = "Create User Account Step";
    private static final String INSERT_USER_ACCOUNT = """
            INSERT INTO user_account(username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives,created_at, active, send_limits, image_name)\s
            VALUES(:username,:firstName,:lastName,:email,:phoneNumber,:numberOfSends,:numberOfReceives,:createdAt,:active,:sendLimits,:imageName)
            """;
    private static final String CSV_USER_ACCOUNT_FIELDS = "username,firstName,lastName,email,phoneNumber,numberOfSends,numberOfReceives,createdAt,active,sendLimits,imageName";

    @Bean
    FlatFileItemReader<UserAccountProcessor.CSVUserAccount> reader() {
        return new FlatFileItemReaderBuilder<UserAccountProcessor.CSVUserAccount>()
                .name("Sample Users Reader")
                .resource(new ClassPathResource("data/sample-users.csv"))
                .delimited()
                .names(CSV_USER_ACCOUNT_FIELDS.split(","))
                .linesToSkip(1)
                .targetType(UserAccountProcessor.CSVUserAccount.class)
                .build();
    }

    @Bean
    JdbcBatchItemWriter<UserAccountEntity> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<UserAccountEntity>()
                .sql(INSERT_USER_ACCOUNT)
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    Step createUserAccountStep(final JobRepository jobRepository,
                               final DataSourceTransactionManager txManager,
                               final FlatFileItemReader<UserAccountProcessor.CSVUserAccount> reader,
                               final JdbcBatchItemWriter<UserAccountEntity> writer,
                               final ItemProcessor<UserAccountProcessor.CSVUserAccount, UserAccountEntity> userAccountProcessor,
                               final ItemWriteListener<UserAccountEntity> listener) {

        return new StepBuilder(STEP_NAME, jobRepository)
                .<UserAccountProcessor.CSVUserAccount, UserAccountEntity>chunk(3, txManager)
                .reader(reader)
                .writer(writer)
                .processor(userAccountProcessor)
                .listener(listener)
                .build();
    }
}
