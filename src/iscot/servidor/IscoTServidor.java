package iscot.servidor;

import java.io.IOException;
import java.net.ServerSocket; 
import java.net.Socket; //representa a ligacao com um cliente

public class IscoTServidor {
    
public static void main(String[]args) { 
   if(args.length != 1) { //servidor espera um argumento que é o nr do porto onde vai ficar à escuta
    System.out.println("Insira o porto do servidor");
    return;
   }

   int porto;

   try{
     porto = Integer.parseInt(args[0]);
   }catch (NumberFormatException e){
    System.err.println("porto tem de ser um numero");
    return;
   }

   try(ServerSocket servidor = new ServerSocket(porto)){//abre um porto de escuta no servidor e fica a espera de ligacoes
    System.out.println("à espera de clientes no porto" + porto);

    while(true){ //ciclo infinito que permite estar à espera de clientes sem fechar após o primeiro cliente

        Socket cliente = servidor.accept(); //fica a espera que um cliente se ligue nesse porto(accept)
                                              //qd o cliente liga, o servidor devolve um socket(representa a ligacao) e fica guardado em cliente
        System.out.println("cliente ligado: "+ cliente.getRemoteSocketAddress());

        DealWithCliente dealWithCliente = new DealWithCliente(cliente);
        dealWithCliente.start();
    
  } 
   
   }catch (IOException e){ 
    System.err.println("problema na logacao:" + e.getMessage());
   }
}
}
