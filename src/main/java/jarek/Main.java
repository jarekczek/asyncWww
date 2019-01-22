package jarek;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Main {
  public static void main(String[] args)
  throws Exception
  {
    SpringApplication app = new SpringApplication(Main.class);
    //app.setWebEnvironment(false);
    app.run(args);
  }
}
