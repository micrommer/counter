package com.micrommer.counter.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.util.*


/**
 * counter (com.micrommer.counter.configuration)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 2:39 PM, Tuesday
 */

@Configuration
class SpringFoxConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo(
                "Smart Counter",
                "A way to communicate with Smart Counter securely",
                "v 0.1",
                "",
                Contact("Iman Buhlool", "", "ibhlool7@gmail.com"),
                "", "", Collections.emptyList())
    }
}