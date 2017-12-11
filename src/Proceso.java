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
    private static Interfaz look_up;

    private static boolean enSeccionCritica = false;

    private static int process_id;
    private static int num_process;
    private static int initialDelay;
    private static boolean bearer;

    private static List<Integer> RN = new ArrayList<Integer>();

    @Override
    public void request(int id, int seq) throws RemoteException, MalformedURLException, NotBoundException {
        LOGGER.info("(" + process_id + ") request desde " + id + " con secuencia " + seq);
        RN.set(id, seq);
        LOGGER.info("(" + process_id + ") RN:" + RN);

        if (!enSeccionCritica && token != null && process_id != id && (RN.get(id) > token.getLN().get(id))) {
            sendToken(id);
        }
    }

    @Override
    public void waitToken() throws RemoteException {
        int waitForToken = 0;
        while (token == null) {
            waitForToken++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (waitForToken % 10 == 0) {
                LOGGER.info("(" + process_id + ") Continúa esperando por token.");
            }
        }
    }

    @Override
    public void takeToken(Token receivedToken) throws RemoteException {
        LOGGER.info("(" + process_id + ") Token recibido.");
        token = receivedToken;
    }

    @Override
    public void kill() throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        LOGGER.info("(" + process_id + ") Recibida orden de terminacion.");
        try {
            Naming.unbind("//localhost/Proceso"+Integer.toString(process_id));
        } catch (NotBoundException e) {
            //Do nothing
        }
        UnicastRemoteObject.unexportObject(this, true);

        new Thread() {
            @Override
            public void run() {
                System.out.print("Shutting down...");
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    // I don't care
                }
                System.out.println("done");
                System.exit(0);
            }

        }.start();
    }

    private void makeRequests() throws RemoteException, NotBoundException, MalformedURLException {
        //Envía el request por el token a todos los otros procesos
        RN.set(process_id, RN.get(process_id) + 1);
        LOGGER.info("(" + process_id + ") Enviando petición de token...");
        LOGGER.info("(" + process_id + ") RN: " + RN);
        for (int i=0; i < num_process; i++) {
            if (i != process_id) {
                look_up = (Interfaz) Naming.lookup("//localhost/Proceso"+i);
                look_up.request(process_id, RN.get(process_id));
            }
        }
    }

    private void dispatchToken() throws MalformedURLException, NotBoundException {
        token.getLN().set(process_id, RN.get(process_id));
        LOGGER.info("(" + process_id + ") Intentando enviar token...");
        LOGGER.info("LN: " + token.getLN());
        LOGGER.info("RN: " + RN);

        for (int j = 0; j < num_process && token != null; j++) {
            if (j == process_id) {
                continue;
            }
            if (RN.get(j) > token.getLN().get(j)) {
                sendToken(j);
                LOGGER.info("(" + process_id + ") Token enviado a proceso " + j);
                break;
            }
        }
    }

    private void sendToken(int id) throws MalformedURLException, NotBoundException {
        assert token != null;

        try {
            LOGGER.info("(" + process_id + ") se envía token a proceso " + id);
            look_up = (Interfaz) Naming.lookup("//localhost/Proceso"+id);
            look_up.takeToken(token);
            token = null;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void wrapperSeccionCritica()
            throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        makeRequests();
        waitToken();
        seccionCritica();
        dispatchToken();
    }

    private void seccionCritica() throws InterruptedException {
        //Tenemos el token?
        if (token != null) {
            //Podemos entrar en la sección crítica
            enSeccionCritica = true;
            LOGGER.info("("+ process_id + ") En seccion critica");
            //Nos quedamos aquí por un rato
            Thread.sleep(10000);
            LOGGER.info("("+ process_id + ") Fuera de seccion critica");
            enSeccionCritica = false;
        }
    }

    private void killProcesess() throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        if (token != null && token.getLN().equals(RN)) {
            //Matamos todos los otros procesos
            for (int i=0; i < num_process; i++) {
                if (i == process_id) {
                    continue;
                }
                LOGGER.info("(" + process_id + ") Matando proceso " + i);
                look_up = (Interfaz) Naming.lookup("//localhost/Proceso" + i);
                look_up.kill();
            }
            kill();
        }
    }



    public Proceso() throws RemoteException {

        super();

    }

    public void compute(int id, int n, int delay, boolean isBearer)
            throws IOException, NotBoundException, InterruptedException {

        //Obtenemos los parámetros pasados como argumentos
        process_id=id;                               //ID del proceso
        num_process=n;                               //Número de procesos en la app distribuída
        initialDelay=delay;                          //Tiempo a esperar para pedir token
        bearer=isBearer;                             //Este nodo es el portador inicial del token?

        FileHandler fh = new FileHandler("LogProceso"+Integer.toString(process_id)+".txt");
        fh.setEncoding("UTF-8");
        LOGGER.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

        LOGGER.info("("+process_id+") Parametros: id="+process_id+", "+num_process+" procesos, "+initialDelay
                +"ms, bearer="+bearer);

        if(bearer) {
            token = Token.instantiate(num_process);
            LOGGER.info("("+process_id+") Token creado.");
        }

        for (int i = 0; i < num_process; i++) RN.add(0);
        LOGGER.info("("+process_id+") RN: "+RN);

        //El prefix de los procesos será //ubicacion/proceso{num}
        Naming.rebind("//localhost/Proceso"+Integer.toString(process_id), new Proceso());
        LOGGER.info("("+process_id+") Proceso registrado en RMI.");

        //Esperamos antes de poder entrar a la sección crítica
        Thread.sleep(initialDelay);

        //Intentamos entrar a nuestra sección crítica
        LOGGER.info("("+process_id+") Entrando a sección crítica...");
        wrapperSeccionCritica();

        LOGGER.info("("+process_id+") Computación finalizada.");

        //Matamos a todos los procesos, si es que este es el último proceso que posee token
        killProcesess();
    }
}
