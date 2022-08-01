package com.caston.create_mvc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("spring.datasource.druid")
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SQLEntity {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
