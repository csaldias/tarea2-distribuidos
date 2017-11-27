import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Proceso extends UnicastRemoteObject implements Interfaz{

    private Token token = null;
    priate int index = 0;
    private boolean enSeccionCritica = false;
    private boolean computacionTerminada = false;

    // for every process the sequence number of the last request this process knows about.
    private List<Integer> RN;

    @Override
    public void request(int id, int seq) throws RemoteException {
        System.out.println("(" + index + ") request desde " + id + " con secuencia " + seq);
        RN.set(id, seq);
        System.out.println("(" + index + ") RN:" + RN);

        if (!enSeccionCritica && token != null && index != id && (RN.get(id) > token.getTN().get(id))) {
            //Enviar token a nodo solicitante
            //Invocar takeToken() en nodo solicitante
        }
    }

    @Override
    public void waitToken() throws RemoteException {

    }

    @Override
    public void takeToken(Token token) throws RemoteException {

    }

    @Override
    public void kill() throws RemoteException {

    }

    private static Interfaz look_up;

    public static void main(String[] args)
            throws MalformedURLException, RemoteException, NotBoundException {

        //El prefix de los procesos será //ubicacion/proceso{num}

        look_up = (Interfaz) Naming.lookup("//localhost/MyServer");
        String txt = JOptionPane.showInputDialog("What is your name?");

        String response = look_up.helloTo(txt);
        JOptionPane.showMessageDialog(null, response);

    }
}
