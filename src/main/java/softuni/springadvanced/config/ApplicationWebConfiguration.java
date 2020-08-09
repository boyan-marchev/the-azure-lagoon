package softuni.springadvanced.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import softuni.springadvanced.web.FaviconInterceptor;
import softuni.springadvanced.web.PageTitleInterceptor;

@Configuration
public class ApplicationWebConfiguration implements WebMvcConfigurer {

    private final PageTitleInterceptor pageTitleInterceptor;
    private final FaviconInterceptor faviconInterceptor;

    @Autowired
    public ApplicationWebConfiguration(PageTitleInterceptor pageTitleInterceptor, FaviconInterceptor faviconInterceptor) {
        this.pageTitleInterceptor = pageTitleInterceptor;
        this.faviconInterceptor = faviconInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.pageTitleInterceptor);
        registry.addInterceptor(this.faviconInterceptor);
    }
}
