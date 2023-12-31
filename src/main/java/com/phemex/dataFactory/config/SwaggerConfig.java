package com.phemex.dataFactory.config;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.config.SwaggerConfig
 * @Date: 2022年05月16日 14:38
 * @Description: Swagger配置类
 */


import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig extends WebMvcConfigurationSupport {

    /**
     * 配置 Swagger 文档生成器
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置 Swagger 文档的基本信息，如标题、描述、联系方式等
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("dataFactory APIs")
                .description("dataFactory项目API管理")
                .termsOfServiceUrl("")
                .contact(new Contact("yuyu.shi", "", "yuyu.shi@cmexpro.com"))
                .version("1.0")
                .build();
    }

    /**
     * 重点
     * 配置 Swagger UI 资源处理器
     * 访问/swagger-ui/index.html来查看生成的 Swagger API 文档
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.
                addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }
}