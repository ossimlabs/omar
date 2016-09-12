package omar.zuul

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean

@EnableZuulProxy
@SpringBootApplication
class OmarZuulServerApplication {

	@Bean
	public SimpleFilter simpleFilter() {
		return new SimpleFilter();
	}

	static void main(String[] args) {
		SpringApplication.run OmarZuulServerApplication, args
	}
}
