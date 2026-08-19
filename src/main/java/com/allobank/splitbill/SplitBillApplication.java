package com.allobank.splitbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SplitBillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SplitBillApplication.class, args);
    }
}
