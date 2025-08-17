package org.bigraphs.model.provider.bigridservice;

import org.bigraphs.spring.data.cdo.CdoServerConnectionString;
import org.bigraphs.spring.data.cdo.CdoTemplate;
import org.bigraphs.spring.data.cdo.SimpleCdoDbFactory;
import org.bigraphs.spring.data.cdo.repository.config.EnableCdoRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ImportResource("classpath:infrastructure.xml")
@EnableCdoRepositories(basePackages = "org.bigraphs.spring.data.cdo.repositories")
public class CdoConfig {

    @Bean
    public CdoTemplate cdoTemplate() throws Exception {
        return new CdoTemplate(new SimpleCdoDbFactory(new CdoServerConnectionString("cdo://localhost:2036/repo1")));
    }
}