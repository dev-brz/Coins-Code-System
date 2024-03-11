package com.cgzt.coinscode.core.config;

import com.cgzt.coinscode.users.adapters.outbound.batch.UserAccountProcessor;
import com.cgzt.coinscode.users.adapters.outbound.batch.UsersWriteListener;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Profile({"!test", "spring-batch-test"})
@Configuration
public class UsersBatchConfig{

    public static final String INSERT_USER_ACCOUNT = """
            INSERT INTO user_account(username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives,created_at, active, send_limits, image_name)\s
            VALUES(:username,:firstName,:lastName,:email,:phoneNumber,:numberOfSends,:numberOfReceives,:createdAt,:active,:sendLimits,:imageName)
            """;
    public static final String CSV_USER_ACCOUNT_FIELDS = "username,firstName,lastName,email,phoneNumber,numberOfSends,numberOfReceives,createdAt,active,sendLimits,imageName";

    @Bean
    public DataSource dataSource(@Value("${spring.datasource.url}") String url,
                                 @Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password,
                                 @Value("${spring.datasource.driverClassName}") String driver){

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driver)
                .build();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public FlatFileItemReader<UserAccountProcessor.CSVUserAccount> reader(){
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
    public JdbcBatchItemWriter<UserAccount> writer(final DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<UserAccount>()
                .sql(INSERT_USER_ACCOUNT)
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(final JobRepository jobRepository, final Step createUserAccountStep){
        return new JobBuilder("Import Sample Users", jobRepository)
                .start(createUserAccountStep)
                .build();
    }

    @Bean
    public Step createUserAccountStep(final JobRepository jobRepository,
                                      final DataSourceTransactionManager txManager,
                                      final FlatFileItemReader<UserAccountProcessor.CSVUserAccount> reader,
                                      final JdbcBatchItemWriter<UserAccount> writer,
                                      final UserAccountProcessor userAccountProcessor,
                                      final UsersWriteListener listener){

        return new StepBuilder("Create User Account Step", jobRepository)
                .<UserAccountProcessor.CSVUserAccount, UserAccount>chunk(3, txManager)
                .reader(reader)
                .writer(writer)
                .processor(userAccountProcessor)
                .listener(listener)
                .build();
    }
}
