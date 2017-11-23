import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Interfaz extends Remote {

    public String helloTo(String name) throws RemoteException;

}