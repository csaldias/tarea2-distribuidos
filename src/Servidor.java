import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Servidor extends UnicastRemoteObject implements Interfaz{

    private static final long serialVersionUID = 1L;

    protected Servidor() throws RemoteException {

        super();

    }

    @Override
    public String helloTo(String name) throws RemoteException{

        System.err.println(name + " is trying to contact!");
        return "Server says hello to " + name;

    }

    public static void main(String[] args){

        try {

            Naming.rebind("//localhost/MyServer", new Servidor());
            System.err.println("Server ready");

        } catch (Exception e) {

            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();

        }

    }

}