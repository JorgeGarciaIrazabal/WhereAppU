package com.application.jorge.whereappu.WebSocket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import java.net.URISyntaxException;
import java.lang.reflect.Modifier;
import com.application.jorge.whereappu.WebSocket.ClientHubs.*;
public class WSHubsApi {//TODO: do not use static functions, we might want different connections
    private static Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .serializeNulls()
            .setDateFormat("yyy/MM/dd HH:mm:ss S")
            .create();
    public WSHubsAPIClient wsClient;
    public SyncHub SyncHub = new SyncHub();
    public TaskHub TaskHub = new TaskHub();
    public UtilsHub UtilsHub = new UtilsHub();
    public PlaceHub PlaceHub = new PlaceHub();
    public ChatHub ChatHub = new ChatHub();
    public LoggingHub LoggingHub = new LoggingHub();

    public WSHubsApi (String uriStr, WebSocketEventHandler webSocketEventHandler) throws URISyntaxException {
        wsClient = new WSHubsAPIClient(uriStr);
        wsClient.setEventHandler(webSocketEventHandler);
    }

    public boolean isConnected(){return wsClient.isConnected();}

    private FunctionResult __constructMessage (String hubName, String functionName, JSONArray argsArray) throws JSONException{
        int messageId= wsClient.getNewMessageId();
        JSONObject msgObj = new JSONObject();
        msgObj.put("hub",hubName);
        msgObj.put("function",functionName);
        msgObj.put("args", argsArray);
        msgObj.put("ID", messageId);
        wsClient.send(msgObj.toJSONString());
        return new FunctionResult(wsClient,messageId);
    }

    private static <TYPE_ARG> void __addArg(JSONArray argsArray, TYPE_ARG arg) throws JSONException {
        try {
            if(arg.getClass().isPrimitive())
                argsArray.add(arg);
            else
                argsArray.add(gson.toJsonTree(arg));
        } catch (Exception e) { //todo: do something with this exception
            JSONArray aux = new JSONArray();
            aux.add(gson.toJsonTree(arg));
            argsArray.add(aux);
        }
    }
    
    public class SyncHub {
        public class Server {
            public static final String HUB_NAME = "SyncHub";
            
            public <TYPE_A> FunctionResult phoneNumbers (TYPE_A phoneNumberArray) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,phoneNumberArray);
                return __constructMessage(HUB_NAME, "phoneNumbers",argsArray);
            }
        }
        public Server server = new Server();
        public Client_SyncHub client = new Client_SyncHub();
    }
    public class TaskHub {
        public class Server {
            public static final String HUB_NAME = "TaskHub";
            
            public <TYPE_A> FunctionResult addTask (TYPE_A newTask) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,newTask);
                return __constructMessage(HUB_NAME, "addTask",argsArray);
            }

            public <TYPE_A> FunctionResult cloneTask (TYPE_A newTask) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,newTask);
                return __constructMessage(HUB_NAME, "cloneTask",argsArray);
            }

            public <TYPE_A> FunctionResult successfullyReceived (TYPE_A taskId) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,taskId);
                return __constructMessage(HUB_NAME, "successfullyReceived",argsArray);
            }
        }
        public Server server = new Server();
        public Client_TaskHub client = new Client_TaskHub();
    }
    public class UtilsHub {
        public class Server {
            public static final String HUB_NAME = "UtilsHub";
            
            public <TYPE_A> FunctionResult setID (TYPE_A id) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,id);
                return __constructMessage(HUB_NAME, "setID",argsArray);
            }
        }
        public Server server = new Server();
        public Client_UtilsHub client = new Client_UtilsHub();
    }
    public class PlaceHub {
        public class Server {
            public static final String HUB_NAME = "PlaceHub";
            
            public <TYPE_A> FunctionResult createPlace (TYPE_A newPlace) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,newPlace);
                return __constructMessage(HUB_NAME, "createPlace",argsArray);
            }

            public <TYPE_A> FunctionResult getPlaces (TYPE_A ownerID) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,ownerID);
                return __constructMessage(HUB_NAME, "getPlaces",argsArray);
            }

            public <TYPE_A> FunctionResult syncPlace (TYPE_A newPlace) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,newPlace);
                return __constructMessage(HUB_NAME, "syncPlace",argsArray);
            }

            public <TYPE_A> FunctionResult updatePlace (TYPE_A newPlace) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,newPlace);
                return __constructMessage(HUB_NAME, "updatePlace",argsArray);
            }
        }
        public Server server = new Server();
        public Client_PlaceHub client = new Client_PlaceHub();
    }
    public class ChatHub {
        public class Server {
            public static final String HUB_NAME = "ChatHub";
            
            public <TYPE_A> FunctionResult sendToAll (TYPE_A message) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,message);
                return __constructMessage(HUB_NAME, "sendToAll",argsArray);
            }
        }
        public Server server = new Server();
        public Client_ChatHub client = new Client_ChatHub();
    }
    public class LoggingHub {
        public class Server {
            public static final String HUB_NAME = "LoggingHub";
            
            public <TYPE_A, TYPE_B, TYPE_C, TYPE_D> FunctionResult logIn (TYPE_A phoneNumber, TYPE_B gcmId, TYPE_C name, TYPE_D email) throws JSONException{
                JSONArray argsArray = new JSONArray();
                __addArg(argsArray,phoneNumber);
				__addArg(argsArray,gcmId);
				__addArg(argsArray,name);
				__addArg(argsArray,email);
                return __constructMessage(HUB_NAME, "logIn",argsArray);
            }
        }
        public Server server = new Server();
        public Client_LoggingHub client = new Client_LoggingHub();
    }
}
    