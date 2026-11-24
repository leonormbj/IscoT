package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.LightSensorEvent;
import iscot.events.RegistrationEvent;

public class LightSensor {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Invalid arguments!");
        } else {
            int porto;
            Areas area = Areas.valueOf(args[2]);
            
            try{
                porto = Integer.parseInt(args[1]); //tem de ser o porto do servidor certo???????????????
            }catch (NumberFormatException e){
                System.err.println("Porto tem de ser um numero");   
                return;
            }

            try{//pedido de registo do cliente no servidor 
        
              Socket socket = new Socket(args[0], porto); //atraves deste socket que o sensor obtém o canal parta enviar e receber inf
              
              //tunel de saida dos dados (aqui estamos só a abrir o tunel para os dados poderem sair????????????????)
              ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream()); 

              RegistrationEvent registo = new RegistrationEvent(area, Type.SENSOR);

              saida.writeObject(registo); //pq fazemos write se ja fazemos getoutputstream?????????????????????
              saida.flush();

              System.out.println("pedido de registo enviado" + registo);
            }catch(IOException e){
                System.err.println("nao foi possivel enviar registo" + e.getMessage());
                return;
        }

            JFrame janela = new JFrame("Luz em " + area);
            janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel painel = new JPanel();
            painel.setLayout(new BorderLayout());

            SensorControl sensorControl = new SensorControl("☀", "Luz - " + area, "%",
                    0, 100, 1, 50,
                    valor -> {
                        Event ev = new LightSensorEvent(area, valor);
                        System.out.println(ev);
                        System.out.println("Mudou para:"+valor);
                    });

            painel.add(sensorControl, BorderLayout.CENTER);
            janela.setContentPane(painel);
            janela.setLocation(600, 400);
            janela.pack();
            janela.setVisible(true);
        
    }
}
}