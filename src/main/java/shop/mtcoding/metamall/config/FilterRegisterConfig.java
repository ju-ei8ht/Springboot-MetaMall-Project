package shop.mtcoding.metamall.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.mtcoding.metamall.core.filter.JwtVerifyFilter;


@Configuration
public class FilterRegisterConfig {
    @Bean
    public FilterRegistrationBean<?> jwtVerifyFilterAdd() {
        FilterRegistrationBean<JwtVerifyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtVerifyFilter());
        registration.addUrlPatterns("/api/products");
        registration.addUrlPatterns("/api/product/*");
        registration.addUrlPatterns("/api/orders");
        registration.addUrlPatterns("/api/order/*");
        registration.addUrlPatterns("/api/admin/*");
//        registration.setOrder(1);
        return registration;
    }
}
