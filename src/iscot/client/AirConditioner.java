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

import iscot.client.gui.ACIcon;
import iscot.enums.AirConditioningMode;
import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaTemperatureOrderEvent;
import iscot.events.AreaVentilationOrderEvent;
import iscot.events.Event;
import iscot.events.RegistrationEvent;

public class AirConditioner {

    private final ACIcon ar = new ACIcon();
    private final JTextArea historico = new JTextArea(8, 25);

    public AirConditioner(Areas area) {
        JFrame janela = new JFrame("Ar condicionado em " + area);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        historico.setEditable(false);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(ar, BorderLayout.CENTER);
        painel.add(new JScrollPane(historico), BorderLayout.SOUTH);

        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }

    public void handle(Event e) {
        if (e instanceof AreaTemperatureOrderEvent) {
            AreaTemperatureOrderEvent ordem = (AreaTemperatureOrderEvent) e;
            ar.setModo(ordem.getMode());
            if (ordem.getMode() == AirConditioningMode.Frio
                    || ordem.getMode() == AirConditioningMode.Aquecimento) {
                ar.setTemperatura(ordem.getTargetTemperature());
                registar("Ar condicionado em " + ordem.getMode()
                        + " (" + ordem.getTargetTemperature() + " °C)");
            } else {
                registar("Ar condicionado em " + ordem.getMode());
            }
        } else if (e instanceof AreaVentilationOrderEvent) {
            AreaVentilationOrderEvent ordem = (AreaVentilationOrderEvent) e;
            AirConditioningMode atual = ar.getModo();
            if (atual == AirConditioningMode.Frio || atual == AirConditioningMode.Aquecimento) {
                System.out.println("Ordem de ventilação ignorada: ar condicionado em " + atual);
            } else if (ordem.isOn()) {
                ar.setModo(AirConditioningMode.Ventilação);
                registar("Ar condicionado em Ventilação");
            } else {
                ar.setModo(AirConditioningMode.Desligado);
                registar("Ar condicionado desligado");
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
            AirConditioner atuador = new AirConditioner(area);

            Thread.sleep(2000);
            atuador.handle(new AreaTemperatureOrderEvent(area, AirConditioningMode.Frio, 22));

            Thread.sleep(3000);
            atuador.handle(new AreaVentilationOrderEvent(area, true));   // deve ser ignorada

            Thread.sleep(3000);
            atuador.handle(new AreaTemperatureOrderEvent(area, AirConditioningMode.Desligado, 0));

            Thread.sleep(3000);
            atuador.handle(new AreaVentilationOrderEvent(area, true));   // agora já liga

            Thread.sleep(3000);
            atuador.handle(new AreaVentilationOrderEvent(area, false));
        }
    }
}