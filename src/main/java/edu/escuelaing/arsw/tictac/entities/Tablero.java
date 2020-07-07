/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.escuelaing.arsw.tictac.entities;

import java.util.Arrays;
import org.json.JSONObject;

/**
 *
 * @author J. Eduardo Arias
 */


public class Tablero {

  public String id;

  public String[] fichas;

  public Tablero() {}

  public Tablero(String id,String[] fichas) {
    this.id = id;
    this.fichas = fichas;
  }

  @Override
  public String toString() {
    return "Tablero [ \"id\"= \""+id +"\",\"fichas\"="+Arrays.toString(fichas)+"]";
      
  }
  
 public JSONObject toJSON() {

        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("fichas", fichas);
        return jo;
    }

}