import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Interfaz extends Remote {

    public String helloTo(String name) throws RemoteException;

}

/*TODO: Implementar:
    request(id, seq):
        Método que registra un request de un proceso remoto. Recibe el id del proceso que hace la petición y el
        número de petición del proceso.
    waitToken():
        Método que le indica a un proceso remoto que debe esperar por el token para realizar la sección
        crítica.
    takeToken(token):
        Método que toma posesión del token en el proceso.
    kill():
        Método que mata el proceso remoto. Debe usar este método para detener el algoritmo de S-K una vez que el
        token haya pasado por todos los nodos del sistema.
    */