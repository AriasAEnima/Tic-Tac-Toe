/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.escuelaing.arsw.tictac.endpoints;

/**
 *
 * @author J. Eduardo Arias
 */
import java.io.IOException;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
@Component
@ServerEndpoint("/TableroService")
public class TableroEndPoint {
    private static final Logger logger
            = Logger.getLogger(TableroEndPoint.class.getName());  
    static ConcurrentHashMap<String,Session[]> partidas= new ConcurrentHashMap<String,Session[]>();
    
    
    Session ownSession = null;
    String partida="";
    

    /* Call this method to send a message to all clients */
    public void send(String msg) {
        try {
            /* Send updates to all open WebSocket sessions */
            for (Session session : partidas.get(partida)) {
                if (session!=null && !session.equals(this.ownSession)) {
                    session.getBasicRemote().sendText(msg);
                }
                logger.log(Level.INFO, "Sent: {0}", msg);
            }
        } catch (IOException e) {
            logger.log(Level.INFO, e.toString());
        }
    }

    @OnMessage
    public void processFicha(String message, Session session) {       
        System.out.println("Ficha received:" + message + ". From session: "
                + session);
       JSONObject json = new JSONObject(message);
       if("".equals(partida) && json.has("id")){
           partida=json.getString("id");
           asignarSigno(session);
       }else{
           this.send(message);
       }      
    }


    public void asignarSigno(Session session){
        String signo;
         if (partidas.containsKey(partida)){
            if( partidas.get(partida)[0]==null){
               partidas.get(partida)[0]=ownSession;
               signo="X";
            }else{
              partidas.get(partida)[1]=ownSession;
               signo="O";
            }            
        }else{
            partidas.put(partida, new Session[]{ownSession,null});
            signo="X";
        }        
        try {
            session.getBasicRemote().sendText("{\"s\":\""+signo+"\"}");
        } catch (IOException ex) {
            Logger.getLogger(TableroEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @OnOpen
    public void openConnection(Session session) {      
        /* Register this connection in the queue */
        ownSession = session;
        logger.log(Level.INFO, "Connection opened.");
        try {
            session.getBasicRemote().sendText("Connection established.");

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @OnClose
    public void closedConnection(Session session) {
        /* Remove this connection from the queue */
        Session[] jugadores=partidas.get(partida);
        if(jugadores!=null){
            if(jugadores[0]==ownSession){
                jugadores[0]=null;
            }else{
                jugadores[1]=null;
            }
            if(jugadores[0]==null && jugadores[1]==null){
                partidas.remove(partida);
            }
        }
        logger.log(Level.INFO, "Connection closed.");
    }

    @OnError
    public void error(Session session, Throwable t) {
        /* Remove this connection from the queue */
        partidas.remove(partida);
        logger.log(Level.INFO, t.toString());
        logger.log(Level.INFO, "Connection error.");
    }
}
