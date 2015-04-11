import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server implements HttpHandler {
	private DateFormat formatter;
	private List<JSONObject> history = new ArrayList<>();
	private MessageExchange messageExchange = new MessageExchange();
	private JSONParser jsonParser = new JSONParser();
	private PrintWriter out;

	public Server() throws IOException {
		this.formatter = DateFormat.getDateTimeInstance();
		this.formatter.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
		out = new PrintWriter(new BufferedWriter(new FileWriter(new File("serverlog.txt"))));
	}

    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Usage: java Server port");
        else {
            try {
                System.out.println("Server is starting...");
                Integer port = Integer.parseInt(args[0]);
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                System.out.println("Server started.");
                server.createContext("/chat", new Server());
                server.setExecutor(null);
                server.start();
            } catch (IOException e) {
                System.out.println("Error creating http server: " + e);
            }
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        out.println(formatter.format(new Date()) + " request begin");
        out.flush();

        String response = "";

        if ("GET".equals(httpExchange.getRequestMethod())) {
            response = doGet(httpExchange);
        } else if ("POST".equals(httpExchange.getRequestMethod())) {
            doPost(httpExchange);
        } else if ("PUT".equals(httpExchange.getRequestMethod()) || "DELETE".equals(httpExchange.getRequestMethod())) {
            System.out.println("PUT&&DELETE");
            doPutOrDelete(httpExchange);
        } else if ("OPTIONS".equals(httpExchange.getRequestMethod())) {
            System.out.println("OPTIONS");
            response = "";
        }
        else {
            response = "Unsupported http method: " + httpExchange.getRequestMethod();
            out.println(formatter.format(new Date()) + " " + response);
            out.flush();
        }

        sendResponse(httpExchange, response);
        out.println(formatter.format(new Date())  + " request end");
        out.flush();
    }

    private void put(ArrayList<JSONObject> messages, int i){
        for (int j = 0; j < messages.size(); j++){
            if((messages.get(j).get("id").toString()).compareTo(history.get(i).get("id").toString()) == 0){
                messages.get(j).put("text", history.get(i).get("text"));
                messages.get(j).put("date", history.get(i).get("date"));
                messages.get(j).put("method", "PUT");
                break;
            }
        }
    }

    private void del(ArrayList<JSONObject> messages, int i){
        for (int j = 0; j < messages.size(); j++){
            if((messages.get(j).get("id").toString()).compareTo(history.get(i).get("id").toString()) == 0){
                messages.get(j).remove("text");
                messages.get(j).put("date", history.get(i).get("date"));
                messages.get(j).put("method", "DELETE");
                break;
            }
        }
    }

    private void post(ArrayList<JSONObject> messages, int i){
        JSONObject msg = new JSONObject(history.get(i));
        messages.add(msg);
    }

    private ArrayList<JSONObject> getMessages(int index){
        ArrayList<JSONObject> messages = new ArrayList<>();
        for (int i = 0; i < index; i++){
            if (history.get(i).get("method") == "PUT")
                put(messages, i);
            else if (history.get(i).get("method") == "DELETE")
                del(messages, i);
            else if (history.get(i).get("method") == "POST")
                post(messages, i);
            else {
                System.out.println("Wrong message..");
            }
        }
        return messages;
    }

    private ArrayList<JSONObject> difference(ArrayList<JSONObject> newList, ArrayList<JSONObject> oldList){
        ArrayList<JSONObject> difference = new ArrayList<>();
        for (int i = 0; i < oldList.size(); i++){
            if(!newList.get(i).equals(oldList.get(i))){
            //if (newList.get(i).toJSONString().compareTo(oldList.get(i).toJSONString()) != 0){
                System.out.println("HAHAHAH");
                difference.add(newList.get(i));
            }
        }
        for (int i = oldList.size(); i < newList.size(); i++){
            difference.add(newList.get(i));
        }
        return difference;
    }

    private String doGet(HttpExchange httpExchange) {
        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> map = queryToMap(query);
            String token = map.get("token");

            if (token != null && !"".equals(token)) {
            	String start = formatter.format(new Date());
                out.println(start + " request method: GET");
                out.println(start + " request parameters: token: " + token);
                out.flush();

                int index = messageExchange.getIndex(token);
                return messageExchange.getServerResponse(difference(getMessages(history.size()), getMessages(index)));
            } else {
                return "Token query parameter is absent in url: " + query;
            }
        }
        out.println(formatter.format(new Date()) + " Absent query in url");
        out.flush();
        return  "Absent query in url";
    }

    private void doPost(HttpExchange httpExchange) {
        try {
            JSONObject msg = messageExchange.getClientMessage(httpExchange.getRequestBody());
            msg.put("method", "POST");

            String start = formatter.format(new Date());
            out.println(start + " request method: POST");
            out.println(start + " request parameters: " + msg);
            out.flush();

            System.out.println("Get Message from User: " + msg);

            history.add(msg);
        } catch (ParseException e) {
            out.println(formatter.format(new Date()) + " Invalid user message");
            out.flush();
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }
    }

    private void doPutOrDelete(HttpExchange httpExchange){
        try{
            JSONObject newMsg = new JSONObject();
            newMsg.put("date", formatter.format(new Date()));

            JSONObject newParams = messageExchange.getClientMessage(httpExchange.getRequestBody());
            System.out.println(newParams);

            if ("DELETE".equals(httpExchange.getRequestMethod())){
                String start = formatter.format(new Date());
                out.println(start + " request method: DELETE");
                out.println(start + " request parameters: " + newParams);
                out.flush();
            }

            else {
                String start = formatter.format(new Date());
                out.println(start + " request method: PUT");
                out.println(start + " request parameters: " + newParams);
                out.flush();
            }

            newMsg.put("id", newParams.get("id"));

            for(JSONObject msg: history){
                if (msg.get("id").equals(newParams.get("id"))){
                    if ("DELETE".equals(httpExchange.getRequestMethod())){
                        newMsg.put("method", "DELETE");
                        System.out.println(newMsg);
                        history.add(newMsg);
                        System.out.println(history);
                    } else if (msg.get("method") != "DELETE"){
                        newMsg.put("method", "PUT");
                        newMsg.put("text", newParams.get("text"));
                        history.add(newMsg);
                    }
                    break;
                }
            }
            System.out.println(history);
        }
        catch(ParseException e){
            out.println(formatter.format(new Date()) + " Invalid user message");
            out.flush();
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange httpExchange, String response){
        try {
        	String start = formatter.format(new Date());
            try{
                JSONObject resp = (JSONObject) jsonParser.parse(response.trim());
                out.println(start + " server response");
                out.println(start + "response parameters: token: " + resp.get("token") + " messages: " + resp.get("messages"));
                out.flush();
            }
            catch (ParseException p){}
            
            byte[] bytes = response.getBytes();
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin","*");
            if("OPTIONS".equals(httpExchange.getRequestMethod())) {
                headers.add("Access-Control-Allow-Methods","PUT, DELETE, POST, GET, OPTIONS");
            }      
            httpExchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}