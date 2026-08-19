package com.yujian.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j / Swagger2 文档配置
 * <p>
 * 启动后访问：http://localhost:8081/doc.html
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

    /**
     * 扫描 admin 控制器，生成接口文档
     *
     * @return Swagger Docket
     */
    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yujian.admin.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 文档标题与描述
     *
     * @return API 信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("宇健口腔 Admin API")
                .description("诊所管理 / 员工管理 / 角色设置 / 权限管理")
                .contact(new Contact("Yujian", "", ""))
                .version("1.0.0")
                .build();
    }
}
