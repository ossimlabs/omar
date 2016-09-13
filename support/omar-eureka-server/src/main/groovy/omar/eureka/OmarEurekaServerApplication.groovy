package omar.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer

@SpringBootApplication
class OmarEurekaServerApplication {

	static void main(String[] args) {
		SpringApplication.run OmarEurekaServerApplication, args
	}
}
