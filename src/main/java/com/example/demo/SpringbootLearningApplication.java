package com.example.demo;

import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SpringBootApplication
@ServletComponentScan
@Slf4j
@RestController
@RequestMapping("/test")
public class SpringbootLearningApplication {

    @RequestMapping("/test")
    public String test(@RequestParam String request) {
        return "response: " + request;
    }

    @GetMapping("/demo")
    //@RequestMapping(value = "/demo", method = RequestMethod.POST)
    public String demoValid(@Valid User user) {
        return user.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootLearningApplication.class, args);
        log.info("服务启动。。。。。。。。。。。。。");
    }
}
