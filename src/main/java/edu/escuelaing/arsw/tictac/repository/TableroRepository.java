/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.escuelaing.arsw.tictac.repository;

/**
 *
 * @author J. Eduardo Arias
 */

import edu.escuelaing.arsw.tictac.entities.Tablero;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface TableroRepository extends MongoRepository<Tablero, String> {

  

}