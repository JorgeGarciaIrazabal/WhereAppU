package com.application.jorge.whereappu.WebSocket;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.JSONException;
import java.net.URISyntaxException;
import org.json.simple.JSONObject;

public class WSServer {//TODO: do not use static functions, we might want different connections
    private static Gson gson = new Gson();
    public static WSConnection connection;

    public static void init(String uriStr, WebSocketEventHandler webSocketEventHandler) throws URISyntaxException {
        connection = new WSConnection(uriStr);
        connection.setEventHandler(webSocketEventHandler);
        connection.connect();
    }

    public static boolean isConnected(){return connection.isConnected();}

    private static FunctionResult constructMessage (String hubName, String functionName, JSONArray argsArray) throws JSONException{
        int messageId= connection.getNewMessageId();
        JSONObject msgObj = new JSONObject();
        msgObj.put("hub",hubName);
        msgObj.put("function",functionName);
        msgObj.put("args", argsArray);
        msgObj.put("ID", messageId);
        connection.send(msgObj.toJSONString());
        return new FunctionResult(connection,messageId);
    }

    private static <TYPE_ARG> void addArg(JSONArray argsArray, TYPE_ARG arg) throws JSONException {
        try {
            argsArray.add(arg);
        } catch (Exception e) {
            JSONArray aux = new JSONArray();
            aux.add(gson.toJson(arg));
            argsArray.add(aux);
        }
    }
    
    public static class SyncHub {
        public static final String HUB_NAME = "SyncHub";
        
        public static <TYPE_A> FunctionResult phoneNumbers (TYPE_A phoneNumberArray) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,phoneNumberArray);
            return constructMessage(HUB_NAME, "phoneNumbers",argsArray);
        }
    }
    public static class TaskHub {
        public static final String HUB_NAME = "TaskHub";
        
        public static <TYPE_A, TYPE_B, TYPE_C, TYPE_D> FunctionResult addTask (TYPE_A body, TYPE_B creatorId, TYPE_C receiverId, TYPE_D createdTime) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,body);
			addArg(argsArray,creatorId);
			addArg(argsArray,receiverId);
			addArg(argsArray,createdTime);
            return constructMessage(HUB_NAME, "addTask",argsArray);
        }
    }
    public static class Places {
        public static final String HUB_NAME = "Places";
        
        public static <TYPE_A, TYPE_B, TYPE_C, TYPE_D, TYPE_E> FunctionResult createPlace (TYPE_A name, TYPE_B userId, TYPE_C icon, TYPE_D createdTime, TYPE_E type) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,name);
			addArg(argsArray,userId);
			addArg(argsArray,icon);
			addArg(argsArray,createdTime);
			addArg(argsArray,type);
            return constructMessage(HUB_NAME, "createPlace",argsArray);
        }

        public static <TYPE_A, TYPE_B, TYPE_C, TYPE_D, TYPE_E, TYPE_F> FunctionResult updatePlace (TYPE_A id, TYPE_B name, TYPE_C userId, TYPE_D icon, TYPE_E createdTime, TYPE_F type) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,id);
			addArg(argsArray,name);
			addArg(argsArray,userId);
			addArg(argsArray,icon);
			addArg(argsArray,createdTime);
			addArg(argsArray,type);
            return constructMessage(HUB_NAME, "updatePlace",argsArray);
        }
    }
    public static class ChatHub {
        public static final String HUB_NAME = "ChatHub";
        
        public static <TYPE_A> FunctionResult sendToAll (TYPE_A message) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,message);
            return constructMessage(HUB_NAME, "sendToAll",argsArray);
        }
    }
    public static class LoggingHub {
        public static final String HUB_NAME = "LoggingHub";
        
        public static <TYPE_A, TYPE_B, TYPE_C, TYPE_D> FunctionResult logIn (TYPE_A phoneNumber, TYPE_B gcmId, TYPE_C name, TYPE_D email) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,phoneNumber);
			addArg(argsArray,gcmId);
			addArg(argsArray,name);
			addArg(argsArray,email);
            return constructMessage(HUB_NAME, "logIn",argsArray);
        }
    }
    public static class PlaceConfigHub {
        public static final String HUB_NAME = "PlaceConfigHub";
        
        public static <TYPE_A> FunctionResult createPlace (TYPE_A place) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,place);
            return constructMessage(HUB_NAME, "createPlace",argsArray);
        }

        public static <TYPE_A> FunctionResult editPlace (TYPE_A place) throws JSONException{
            JSONArray argsArray = new JSONArray();
            addArg(argsArray,place);
            return constructMessage(HUB_NAME, "editPlace",argsArray);
        }
    }
}
    