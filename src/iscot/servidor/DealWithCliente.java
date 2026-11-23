package iscot.servidor;

import java.net.Socket;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import iscot.events.Event;
import iscot.events.RegistrationEvent;

public class DealWithCliente extends Thread{

    private Socket cliente;
    
    public void dealWithCliente(Socket cliente){
        this.cliente = cliente;
    }

    public void run(){
        
        try{
            ObjectInputStream ligacao = new ObjectInputStream(cliente.getInputStream()); //recontroi se os dados da ligacao(que sao acedidos pelo getinputstream) em objetos java
            
            while(true){
                
            }
        }
    } 
}
