// src/main/java/com/railse/workforcemgmt/WorkforcemgmtApplication.java
package com.railse.workforcemgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WorkforcemgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkforcemgmtApplication.class, args);
    }
}
