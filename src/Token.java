import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Token implements Serializable{
    private List<Integer> LN;
    Queue Q = new LinkedList();
    private static boolean isInstantiated = false;

    /**
     * The constructor is made private as Token is a singleton created only once for the whole system.
     * @param numProcesses - number of processes in a system
     */
    private Token(int numProcesses){
        LN = new ArrayList<Integer>();
        for (int i = 0; i < numProcesses; i++){
            LN.add(0);
        }

    }

    /**
     * Instantiates token if executes for the first time
     * @return token after first execution, null otherwise
     */
    public static Token instantiate(int numProcesses){
        if (!isInstantiated){
            isInstantiated = true;
            return new Token(numProcesses);
        }
        return null;
    }

    public List<Integer> getLN() {
        return LN;
    }
}
