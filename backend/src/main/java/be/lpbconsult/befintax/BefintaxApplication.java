package be.lpbconsult.befintax;

import be.lpbconsult.befintax.wallet.mapper.AssetMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication(scanBasePackages = {"be.lpbconsult.befintax"})
public class BefintaxApplication {
    public static void main(String[] args) {
        SpringApplication.run(BefintaxApplication.class, args);
    }

}
