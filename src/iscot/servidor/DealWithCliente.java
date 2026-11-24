package iscot.servidor;

import java.net.Socket;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import iscot.events.Event;
import iscot.events.RegistrationEvent;

public class DealWithCliente extends Thread{

    private Socket cliente;
    
    public DealWithCliente(Socket cliente){
        this.cliente = cliente;
    }

    public void run(){
        
        try{
            ObjectInputStream ligacao = new ObjectInputStream(cliente.getInputStream()); //recontroi se os dados da ligacao(que sao acedidos pelo getinputstream) em objetos java
            
            while(true){ //ciclo permite receber varios cliente para um unico socket 
                Object recebido = ligacao.readObject(); //o read.object espera por um objeto do cliente
                                                        //fica tipo object pq antes de verificarmos n sabemos que tipo é
                if( recebido instanceof RegistrationEvent){
                    RegistrationEvent registo = (RegistrationEvent) recebido; //cast permite guardar nesse tipo e ter acesso ao que a classe oferece
                    System.out.println("Registo recebido" + registo);
                }else if(recebido instanceof Event){
                    Event evento = (Event) recebido;
                    System.out.println("Evento recebido" + evento);
                }else{
                    System.out.println("O objeto enviado não é um evento");
                }

                }
            }catch(EOFException e){
                System.out.println("ligação fechada");
            }catch (IOException e ){
                System.err.println("erro na ligação");
            }catch(ClassNotFoundException e){
                System.err.println("a classe recebida não foi reconhecida");
            }
            finally { //finnaly executa quando a thread termina mm que tenha havido erro
                try {
                    cliente.close(); //fecha se a ligação deste cliente
                }catch(IOException e){
                    System.err.println("erro ao fechar ligação" + e.getMessage());
                }
            }
        }
    } 

