import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.List;
import java.util.Date;
import java.util.Random;

public class MessageExchange {
    private JSONParser jsonParser = new JSONParser();

    public String getToken(int index) {
        Integer number = index * 8 + 11;
        return "TN" + number + "EN";
    }

    public int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public String getUniqueId(){
        Date date = new Date();
        Random rand = new Random(date.getTime());
        return ((Integer)Math.abs(rand.nextInt() * rand.nextInt())).toString();
    }

    public String getServerResponse(List<JSONObject> messages) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages", messages);
        jsonObject.put("token", getToken(messages.size()));
        return jsonObject.toJSONString();
    }

    public String getClientSendMessageRequest(String text, String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", text);
        jsonObject.put("name", name);
        jsonObject.put("id", getUniqueId());
        jsonObject.put("date", (new Date()).toLocaleString());
        jsonObject.put("isDeleted", "false");
        return jsonObject.toJSONString();
    }

    public JSONObject getClientMessage(InputStream inputStream) throws ParseException {
        return getJSONObject(inputStreamToString(inputStream));
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(baos.toByteArray());
    }
}