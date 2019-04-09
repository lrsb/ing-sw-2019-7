package it.polimi.ingsw;

import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

interface ReceiveMessageInterface extends Remote {
    /**
     * @param x
     * @throws RemoteException
     */
    void receiveMessage(String x) throws RemoteException;

    /**
     * @param observer
     * @throws RemoteException
     */
    void addObserver(Remote observer) throws RemoteException;
}

/**
 *
 */
class RmiClient extends UnicastRemoteObject {
    protected RmiClient() throws RemoteException {
    }

    /**
     * @param args
     */
    static public void main(String[] args) {
        if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());
        ReceiveMessageInterface rmiServer;
        Registry registry;
        String serverAddress = "localhost";//args[0];
        String serverPort = "3232";//args[1];
        String text = "ciaop";//args[2];
        System.out.println("sending " + text + " to " + serverAddress + ":" + serverPort);
        try { // Get the server's stub
            registry = LocateRegistry.getRegistry(serverAddress, 3232);
            rmiServer = (ReceiveMessageInterface) (registry.lookup("rmiServer"));

            // RMI client will give a stub of itself to the server
            Remote aRemoteObj = UnicastRemoteObject.exportObject(new RmiClient(), 0);
            rmiServer.addObserver(aRemoteObj);

            // call the remote method
            rmiServer.receiveMessage(text);
            // update method will be notified
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println(e);
        }
    }

    public void update(String a) throws RemoteException {
        // update should take some serializable object as param NOT Observable
        // and Object
        // Server callsbacks here
    }
}

/**
 *
 */
class RmiServer extends Observable implements ReceiveMessageInterface {
    String address;
    Registry registry;

    /**
     * @throws RemoteException
     */
    public RmiServer() throws RemoteException {
        try {
            address = (InetAddress.getLocalHost()).toString();
        } catch (Exception e) {
            System.out.println("can't get inet address.");
        }
        int port = 3232;
        System.out.println("this address=" + address + ",port=" + port);
        try {
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("rmiServer", this);
        } catch (RemoteException e) {
            System.out.println("remote exception" + e);
        }
    }

    /**
     * @param args
     */
    static public void main(String[] args) {
        try {
            RmiServer server = new RmiServer();
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void receiveMessage(String x) throws RemoteException {
        System.out.println(x);
        setChanged();
        notifyObservers(x + "invoked me");
    }

    /**
     * {@inheritDoc}
     */
    public void addObserver(final Remote observer) throws RemoteException {
        // This is where you plug in client's stub
        super.addObserver(new Observer() {
            @Override
            public void update(Observable o,
                               Object arg) {
                try {
                    ((RmiClient) observer).update((String) arg);
                } catch (RemoteException e) {

                }
            }
        });
    }
}