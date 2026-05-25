package org.jpa.gettingalongwithjpa.jpa.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@TestConfiguration
public class JpaTxTestConfig {

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager tm) {
        TransactionTemplate tx = new TransactionTemplate(tm);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return tx;
    }
}

