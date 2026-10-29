package br.com.fatecads.fatecads.config;

import br.com.fatecads.fatecads.service.DemoDataService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataInitializer implements ApplicationRunner {
    private final DemoDataService data;
    public DemoDataInitializer(DemoDataService data) { this.data = data; }
    @Override public void run(ApplicationArguments args) { data.seed(); }
}
