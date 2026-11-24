package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import iscot.client.gui.VentilatorIcon;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaVentilationOrderEvent;
import iscot.events.Event;
import iscot.events.RegistrationEvent;

public class Ventilator {

    private final VentilatorIcon ventilador = new VentilatorIcon();
    private final JTextArea historico = new JTextArea(8, 25);

    public Ventilator(Areas area) {
        JFrame janela = new JFrame("Ventilador em " + area);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        historico.setEditable(false);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(ventilador, BorderLayout.CENTER);
        painel.add(new JScrollPane(historico), BorderLayout.SOUTH);

        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }

    public void handle(Event e) {
        if (e instanceof AreaVentilationOrderEvent) {
            AreaVentilationOrderEvent ordem = (AreaVentilationOrderEvent) e;
            ventilador.setLigado(ordem.isOn());
            if (ordem.isOn()) {
                registar("Ventilador ligado");
            } else {
                registar("Ventilador desligado");
            }
        }
    }

    private void registar(String acao) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> historico.append(acao + " - " + hora + "\n"));
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println("Invalid arguments!");
        } else {
            int porto;
            Areas area = Areas.valueOf(args[2]);

            try{
                porto = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                System.err.println("o porto tem de ser um numero");
            }

            try{
                Socket socket = new Socket(args[0], porto);

                ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());

                RegistrationEvent registo = new RegistrationEvent(area, Type.ACTUATOR);

                saida.writeObject(registo);
                saida.flush();

                System.out.println("pedido de registo enviado" + registo);
            }catch(IOException e){
                System.err.println("nao foi possivel enviar registo" + e.getMessage());
            }
            
         
            Ventilator atuador = new Ventilator(area);

            Thread.sleep(2000);
            atuador.handle(new AreaVentilationOrderEvent(area, true));

            Thread.sleep(3000);
            atuador.handle(new AreaVentilationOrderEvent(area, false));
        }
    }
}