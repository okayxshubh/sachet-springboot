package in.gov.cybercrime.sachet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final NoCacheInterceptor noCacheInterceptor;

    public WebConfig(NoCacheInterceptor noCacheInterceptor) {
        this.noCacheInterceptor = noCacheInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(noCacheInterceptor)
                .addPathPatterns("/api/**");
    }
}
