import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Proceso extends UnicastRemoteObject implements Interfaz{

    private static final Logger LOGGER = Logger.getLogger( Proceso.class.getName() );

    private static final long serialVersionUID = 1141532719924321124L;
    private static Token token;

    private int seq = 1;
    private boolean enSeccionCritica = false;
    private boolean computacionTerminada = false;
    private static boolean poseeToken = false;

    static int process_id;
    static int num_process;
    static int initialDelay;
    static String bearer;

    private static List<Integer> RN = new ArrayList<Integer>();

    @Override
    public void request(int id, int seq) throws RemoteException {
        LOGGER.info("(" + process_id + ") request desde " + id + " con secuencia " + seq);
        RN.set(id, seq);
        LOGGER.info("(" + process_id + ") RN:" + RN);

        if (!enSeccionCritica && token != null && process_id != id && (RN.get(id) > token.getTN().get(id))) {
            //Enviar token a nodo solicitante
            //Invocar takeToken() en nodo solicitante
            //El token está serializado, se puede usar
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
        System.out.println("(" + process_id + ") Recibida orden de terminacion.");
        System.exit(0);
    }

    public void makeRequests() {

    }

    private static Interfaz look_up;

    protected Proceso() throws RemoteException {

        super();

    }

    public static void main(String[] args)
            throws IOException, NotBoundException, InterruptedException {

        //Obtenemos los parámetros pasados como argumentos
        process_id=Integer.parseInt(args[0]);       //ID del proceso
        num_process=Integer.parseInt(args[1]);      //Número de procesos en la app distribuída
        initialDelay=Integer.parseInt(args[2]);     //Tiempo a esperar para pedir token
        bearer=args[3];                             //Este nodo es el portador inicial del token?

        FileHandler fh = new FileHandler("LogProceso"+Integer.toString(process_id)+".txt");
        fh.setEncoding("UTF-8");
        LOGGER.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        LOGGER.info("("+process_id+") Parametros: id="+process_id+", "+num_process+" procesos, "+initialDelay
                +"ms, bearer="+bearer);

        if(bearer.equals("true")) {
            token = Token.instantiate(num_process);
            poseeToken = true;
            LOGGER.info("("+process_id+") Token creado.");
        }

        for (int i = 0; i < num_process; i++) RN.add(0);
        LOGGER.info("("+process_id+") Lista RN: "+RN);

        //El prefix de los procesos será //ubicacion/proceso{num}
        Naming.rebind("//localhost/Proceso"+Integer.toString(process_id), new Proceso());
        LOGGER.info("("+process_id+") Process has been binded.");

        //Esperamos antes de poder entrar a la sección crítica
        Thread.sleep(initialDelay);

        //Intentamos entrar a nuestra sección crítica
        LOGGER.info("("+process_id+") Entrando a sección crítica...");

        //look_up = (Interfaz) Naming.lookup("//localhost/MyServer");
        //String txt = JOptionPane.showInputDialog("What is your name?");

        //String response = look_up.helloTo(txt);
        //JOptionPane.showMessageDialog(null, response);

    }
}
