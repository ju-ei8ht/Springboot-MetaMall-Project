package shop.mtcoding.metamall.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import shop.mtcoding.metamall.config.interceptor.LoginInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/products")
                .addPathPatterns("/api/product/*")
                .addPathPatterns("/api/orders")
                .addPathPatterns("/api/order/*")
                .addPathPatterns("/api/admin/*");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*") // GET, POST, PUT, DELETE (Javascript 요청 허용)
                .allowedOriginPatterns("*") // 모든 IP 주소 허용 (프론트 앤드 IP만 허용하게 변경해야함. * 안됨)
                .allowCredentials(true)
                .exposedHeaders("Authorization"); // 옛날에는 디폴트로 브라우저에 노출되어 있었는데 지금은 아님
    }
}
