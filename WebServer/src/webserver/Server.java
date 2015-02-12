package webserver;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable{
    Socket connection;
    ArrayList<String> history;

    public Server(Socket connection){
        this.connection = connection;
        this.history = new ArrayList<String>();
    }

    public static void main(String[] ar){
        int port = 6666; // случайный порт (может быть любое число от 1025 до 65535)
        
        try {
            ServerSocket ss = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Waiting for a client...");

            while(true) {
                Socket connection = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
                System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
                System.out.println();

                Runnable runnable = new Server(connection); //??????
                Thread thread = new Thread(runnable);
                thread.start();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            InputStream sin = connection.getInputStream();
            OutputStream sout = connection.getOutputStream();
            
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            String line = null;
            String name = in.readUTF();
            
            while(true){
                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("The dumb client just sent me this line : " + line);
                System.out.println("I'm sending it back...");
                history.add(name + ": " + line);
                out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
                System.out.println("Waiting for the next line...");
                System.out.println();
                System.out.println(history);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}