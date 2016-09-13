package omar.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.config.server.EnableConfigServer

@EnableConfigServer
@SpringBootApplication
class OmarConfigServerApplication {

	static void main(String[] args) {
		SpringApplication.run OmarConfigServerApplication, args
	}
}
