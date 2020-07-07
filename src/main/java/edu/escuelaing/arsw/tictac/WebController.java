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

import edu.escuelaing.arsw.tictac.entities.Tablero;
import edu.escuelaing.arsw.tictac.repository.TableroRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class WebController {
    @Autowired
  private TableroRepository repository;
    
    @GetMapping("/status")
    public String status() {
         
        
        return "{\"status\":\"Greetings from Spring Boot Ping Pong Synergy "
                + java.time.LocalDate.now() + ", "
                + java.time.LocalTime.now()
                + ". " + "The server is Running! and Add some tableros"+repository.findById("1").get()+"\"}";
    }
    
    @PostMapping("/guardar")   
    @ResponseBody
    public void reiniciar(@RequestParam(value = "id") String id, @RequestParam(value = "fichas") String[] fichas) {        
         repository.save(new Tablero(id, fichas));
    }
    
        
    @PostMapping("/partida")
    public String sign(@RequestParam(value = "id") String id) {      

        Optional<Tablero> t=repository.findById(id);
        if(t.isPresent()){
            System.out.println("Enviando .... " + repository.findById(id).get().toJSON().toString());
           return t.get().toJSON().toString();
        }else{
            return new Tablero(id,new String[]{"","","","","","","","",""}).toJSON().toString();
        }              
     
    }    

}
