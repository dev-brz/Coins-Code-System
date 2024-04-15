package com.cgzt.coinscode.core.config.batch;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
class CoinsBatchConfig {
    private static final String INSERT_COINS = """
            INSERT INTO coins(uid,name,image_name,description,amount,user_account_id)
            VALUES(:uid,:name,:imageName,:description,:amount,(SELECT id FROM user_account WHERE username=:userAccount.username))
            """;
    private static final String CSV_COINS_FIELDS = "uid,username,name,image_name,description,amount";

    @Bean
    FlatFileItemReader<CoinEntity> coinsReader() {
        return new FlatFileItemReaderBuilder<CoinEntity>()
                .name("Sample Coins Reader")
                .resource(new ClassPathResource("data/sample-coins.csv"))
                .delimited()
                .names(CSV_COINS_FIELDS.split(","))
                .linesToSkip(1)
                .fieldSetMapper(it -> {
                    var user = new UserAccountEntity();
                    var coin = new CoinEntity();

                    user.setUsername(it.readString("username"));
                    coin.setUid(it.readString("uid"));
                    coin.setUserAccount(user);
                    coin.setName(it.readString("name"));
                    coin.setImageName(it.readString("image_name"));
                    coin.setDescription(it.readString("description"));
                    coin.setAmount(it.readBigDecimal("amount"));

                    return coin;
                })
                .build();
    }

    @Bean
    JdbcBatchItemWriter<CoinEntity> coinsWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CoinEntity>()
                .sql(INSERT_COINS)
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    Step createCoinsStep(final JobRepository jobRepository,
                         final DataSourceTransactionManager txManager,
                         final FlatFileItemReader<CoinEntity> coinsReader,
                         final JdbcBatchItemWriter<CoinEntity> coinsWriter,
                         final ItemWriteListener<CoinEntity> coinsWriteListener) {

        return new StepBuilder("Create Coin Step", jobRepository)
                .<CoinEntity, CoinEntity>chunk(3, txManager)
                .reader(coinsReader)
                .writer(coinsWriter)
                .listener(coinsWriteListener)
                .build();
    }
}
