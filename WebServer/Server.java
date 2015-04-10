import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	private List<JSONObject> history = new ArrayList<JSONObject>();
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
                //String serverHost = InetAddress.getLocalHost().getHostAddress();
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
    	String start = formatter.format(new Date());
        out.println(start + " request begin");
        out.flush();

        String response = "";

        if ("GET".equals(httpExchange.getRequestMethod())) {
            response = doGet(httpExchange);
        } else if ("POST".equals(httpExchange.getRequestMethod())) {
            doPost(httpExchange);
        } else if ("PUT".equals(httpExchange.getRequestMethod()) || "DELETE".equals(httpExchange.getRequestMethod())) {
            doPutOrDelete(httpExchange);
            if ("PUT".equals(httpExchange.getRequestMethod()))
                response = doGet(httpExchange);
        } else if ("OPTIONS".equals(httpExchange.getRequestMethod())) {
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
                return messageExchange.getServerResponse(history.subList(index, history.size()));
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
            JSONObject newMsg;
            JSONObject newParams = messageExchange.getClientMessage(httpExchange.getRequestBody());
            for (Iterator <JSONObject> it = history.iterator(); it.hasNext();){
                newMsg = it.next();
                if (newMsg.get("id").equals(newParams.get("id"))){
                    newMsg.put("date", (new Date().toLocaleString()));
                    if ("DELETE".equals(httpExchange.getRequestMethod())){
                        newMsg.put("text", "'is deleted'");
                        newMsg.put("isDeleted", true);
                    } else if (!(Boolean)newMsg.get("isDeleted")){
                        newMsg.put("isEdited", true);
                        newMsg.put("text", newParams.get("text"));
                        System.out.println(newParams.get("text"));
                    }
                }
            }
            System.out.println(history);
        }
        catch(ParseException e){
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