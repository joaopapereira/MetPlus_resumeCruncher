package org.metplus.curriculum;

import org.metplus.curriculum.domain.cruncher.CrunchersList;
import org.metplus.curriculum.domain.cruncher.MatcherList;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@SpringBootApplication
public class Application  extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Qualifier("crunchersList")
    public CrunchersList crunchersList() {
        return new CrunchersList();
    }

    @Bean
    @Qualifier("matchersList")
    public MatcherList matchersList() {
        return new MatcherList();
    }
}