package com.mluo.microservices.composite.product;

import com.mluo.microservices.composite.product.services.ProductCompositeIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.LinkedHashMap;

@EnableSwagger2WebFlux
@SpringBootApplication
@ComponentScan("com.mluo")
public class ProductCompositeServiceApplication {
    @Value("${api.common.version}")
    String apiVersion;
    @Value("${api.common.title}")
    String apiTitle;
    @Value("${api.common.description}")
    String apiDescription;
    @Value("${api.common.termsOfServiceUrl}")
    String apiTermsOfServiceUrl;
    @Value("${api.common.license}")
    String apiLicense;
    @Value("${api.common.licenseUrl}")
    String apiLicenseUrl;
    @Value("${api.common.contact.name}")
    String apiContactName;
    @Value("${api.common.contact.url}")
    String apiContactUrl;
    @Value("${api.common.contact.email}")
    String apiContactEmail;

    @Autowired
    HealthAggregator healthAggregator;

    @Autowired
    ProductCompositeIntegration integration;

    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }

    @Bean
    ReactiveHealthIndicator coreServices() {

        ReactiveHealthIndicatorRegistry registry = new DefaultReactiveHealthIndicatorRegistry(new LinkedHashMap<>());

        registry.register("product", () -> integration.getProductHealth());
        registry.register("recommendation", () -> integration.getRecommendationHealth());
        registry.register("review", () -> integration.getReviewHealth());

        return new CompositeReactiveHealthIndicator(healthAggregator, registry);
    }
}
