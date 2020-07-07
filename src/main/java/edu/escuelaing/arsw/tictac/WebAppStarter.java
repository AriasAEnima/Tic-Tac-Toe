/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.escuelaing.arsw.tictac;

/**
 *
 * @author J. Eduardo Arias
 */
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebAppStarter {

  public static void main(String[] args) {
    SpringApplication app=new SpringApplication(WebAppStarter.class);
    app.setDefaultProperties(Collections.singletonMap("spring.data.mongodb.uri", 
            "mongodb+srv://eduardo:eduardo@mongodbtest.bpng2.mongodb.net/TicTac"));  
     
    app.run(args);
  } 

  

}