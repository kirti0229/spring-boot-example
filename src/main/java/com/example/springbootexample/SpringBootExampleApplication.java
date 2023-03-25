package com.example.springbootexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootExampleApplication.class, args);
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/task")
    public String task(@RequestParam String fname,@RequestParam (required = false)String mname,@RequestParam String lname ) {
        String goodName = "";
        if (mname==""){
            goodName = fname.concat(" ").concat(lname);
        }else{
           goodName = fname.concat(" ").concat(mname).concat(" ").concat(lname);
        }

        return String.format( goodName.toUpperCase());
    }

}
