package com.redis.rediscachingjava.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@PropertySource(ignoreResourceNotFound = true, value = "classpath:application.properties")
@Controller
public class Property {
    @Autowired
    private Environment env;

    public String getPropertyPath() {
        try {
            return env.getProperty("redis.endpoint.uri");
        } catch (NullPointerException e){
            return "redis://localhost:6379";
        }
    }
}
