import java.io.IOException;
import java.rmi.NotBoundException;

public class Main_Proceso {
    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        //Obtenemos los parámetros pasados como argumentos
        int process_id=Integer.parseInt(args[0]);       //ID del proceso
        int num_process=Integer.parseInt(args[1]);      //Número de procesos en la app distribuída
        int initialDelay=Integer.parseInt(args[2]);     //Tiempo a esperar para pedir token
        String bearer=args[3];                             //Este nodo es el portador inicial del token?

        Proceso pro = new Proceso(); //Se crea el cliente
        System.out.println("Iniciando proceso "+process_id);
        pro.compute(process_id, num_process, initialDelay, bearer.equals("true")); //Se inicia el proceso
    }
}
