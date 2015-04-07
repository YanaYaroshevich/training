import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;

public class Client implements Runnable {
    private List<JSONObject> history = new ArrayList<JSONObject>();
    private MessageExchange messageExchange = new MessageExchange();
    private String host;
    private Integer port;
    private PrintWriter out;
    private Date d;
    private String name;

    public Client(String host, Integer port) throws IOException{
        this.host = host;
        this.port = port;
        out = new PrintWriter(new BufferedWriter(new FileWriter(new File("clientlog.txt"))));
    }

    public static void main(String[] args) throws IOException{
        if (args.length != 2)
            System.out.println("Usage: java ChatClient host port");
        else {
            System.out.println("Connection to server...");
            String serverHost = args[0];
            Integer serverPort = Integer.parseInt(args[1]);
            Client client = new Client(serverHost, serverPort);
            client.nameInput();
            new Thread(client).start();
            System.out.println("Connected to server: " + serverHost + ":" + serverPort);
            client.listen();
        }
    }

    private void nameInput() throws IOException{
        System.out.print("Enter your name, please ");
        Scanner sc = new Scanner(System.in);
        name = sc.nextLine();
        System.out.println("Your name: " + name);
    }

    private HttpURLConnection getHttpURLConnection() throws IOException {
        URL url = new URL("http://" + host + ":" + port + "/chat?token=" + messageExchange.getToken(history.size()));
        d = new Date();
        out.println(d.toLocaleString() + " request parameters: url: " + url);
        out.flush();
        return (HttpURLConnection) url.openConnection();
    }

    public List<JSONObject> getMessages() {
        List<JSONObject> list = new ArrayList<JSONObject>();
        HttpURLConnection connection = null;
        try {
            d = new Date();
            out.println(d.toLocaleString() + " request begin");
            out.println(d.toLocaleString() + " request method: GET");
            out.flush();

            connection = getHttpURLConnection();
            connection.connect();
            String response = messageExchange.inputStreamToString(connection.getInputStream());
            JSONObject jsonObject = messageExchange.getJSONObject(response);
            JSONArray jsonArray = (JSONArray) jsonObject.get("messages");

            d = new Date();
            out.println(d.toLocaleString() + " server response parameters: messages: " + jsonArray + " token: " + jsonObject.get("token"));
            out.flush();

            for (Object o : jsonArray) {
                System.out.println(o.toString());
                list.add((JSONObject)o);
            }

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("ERROR: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
                d = new Date();
                out.println(d.toLocaleString() + " request end");
                out.flush();
            }
        }

        return list;
    }

    public void sendMessage(String text) {
        HttpURLConnection connection = null;
        try {
            d = new Date();
            out.println(d.toLocaleString() + " request begin");
            out.println(d.toLocaleString() + " request method: POST");
            out.flush();

            connection = getHttpURLConnection();
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            byte[] bytes = messageExchange.getClientSendMessageRequest(text, name).getBytes();
            wr.write(bytes, 0, bytes.length);
            wr.flush();
            wr.close();

            d = new Date();
            out.println(d.toLocaleString() + " request parameters: name: " + name + " text: " + text);
            out.flush();
            connection.getInputStream();

        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
                d = new Date();
                out.println(d.toLocaleString() + " request end");
                out.flush();
            }
        }
    }

    public void listen() {
        while (true) {
            List<JSONObject> list = getMessages();
            if (list.size() > 0) {
                history.addAll(list);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.println("ERROR: " + e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String text = scanner.nextLine();
            sendMessage(text);
        }
    }
}