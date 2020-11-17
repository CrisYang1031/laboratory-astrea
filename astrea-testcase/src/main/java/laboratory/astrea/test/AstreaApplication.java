package laboratory.astrea.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class AstreaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstreaApplication.class);
    }
}
