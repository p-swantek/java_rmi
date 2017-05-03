package task3;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the server object
 * 
 * @author Peter Swantek
 *
 */
public interface GetSearcher extends Remote {

    GraphSearcher getSearcher() throws RemoteException;

}
