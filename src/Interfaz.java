import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Interfaz extends Remote {
    //Método que registra un request de un proceso remoto. Recibe el id del proceso que hace la petición y el
    //número de petición del proceso.
    void request(int id, int seq) throws RemoteException, MalformedURLException, NotBoundException;

    //Método que le indica a un proceso remoto que debe esperar por el token para realizar la sección crítica.
    void waitToken() throws RemoteException;

    //Método que toma posesión del token en el proceso.
    void takeToken(Token token) throws RemoteException;

    //Método que mata el proceso remoto. Debe usar este método para detener el algoritmo de S-K una vez que el
    //token haya pasado por todos los nodos del sistema.
    void kill() throws RemoteException, MalformedURLException, NotBoundException, InterruptedException;

}