package spring.coupons;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import base.coupons.couponSystemCore.CouponSystem;

@SpringBootApplication
public class Application {
	@Value("${server.port:8080}")
	private int port;

	@PostConstruct
	public void init() {
		try {
			CouponSystem.getInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
