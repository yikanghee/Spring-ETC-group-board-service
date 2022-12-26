package com.example.groupboardservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableOpenApi
@Configuration
public class SwaggerConfig {

    /**
     * 문서 설명 내용
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("group-board-service")
                .description("this is group board !!")
                .contact(new Contact("Juhyeon-Oh", "https://github.com/ohju96", "ohju96@gmail.com"))
                .license("Juhyoen-Oh")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30) // openAPI 3.0
                .useDefaultResponseMessages(true) // 기본 응답 코드 노출 제외
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.groupboardservice")) // 정의한 api 패키지
                .paths(PathSelectors.any()) // Controller의 모든 API 문서화하기
                .build()
                .apiInfo(apiInfo()); // 문서 정보 저장
    }

}
