package it.polimi.ingsw.rmi;

import java.rmi.Naming;

public class HelloClient {
    public static void main(String[] arg) {
        System.setSecurityManager(new SecurityManager());
        try {
            Hello obj = (Hello) Naming.lookup("//" +
                    "localhost" +
                    "/HelloServer");
            System.out.println(obj.hello());
        } catch (Exception e) {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}