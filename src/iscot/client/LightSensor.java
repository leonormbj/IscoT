package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.Event;
import iscot.events.LightSensorEvent;

public class LightSensor {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Invalid arguments!");
        } else {
            int porto;
            
            try{
                porto = Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                System.err.println("Porto tem de ser um numero");   
                return;
            }

            try{
              try {
              Socket socket = new Socket(args[0], porto);
              System.out.println("Ligado ao servidor : " + socket.getRemoteSocketAddress()); 
            }catch(IOException e){
                System.err.println("Nao foi possivel ligar ao servidor " + e.getMessage());
                return;
            }

            // TODO register with the server
            Areas area = Areas.valueOf(args[2]);

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