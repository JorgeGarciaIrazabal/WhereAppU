package com.application.jorge.whereappu.Tornado;
import android.app.Activity;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class TornadoServer {
    private static Gson gson = new Gson();
    private static TornadoConnection connection;

    public static void init(String uriStr, Activity activity) throws URISyntaxException {
        connection = new TornadoConnection(uriStr);
        connection.mainActivity= activity;
        connection.connect();
    }
    private static <TYPE_ARG> void addArg(JSONArray argsArray, TYPE_ARG arg) throws JSONException {
        try {
            argsArray.put(arg);
        } catch (Exception e) {
            argsArray.put(new JSONObject(gson.toJson(arg)));
        }
    }
    
    public static class TestClass {
        public static final String HUB_NAME = "TestClass";
        
        public static <TYPE_A, TYPE_B, TYPE_C> void tast (TYPE_A a, TYPE_B b, TYPE_C c) throws JSONException{
            JSONObject msgObj = new JSONObject();
            JSONArray argsArray = new JSONArray();
            TornadoServer.addArg(argsArray, a);
			TornadoServer.addArg(argsArray, b);
			TornadoServer.addArg(argsArray,c);
            msgObj.put("hub",HUB_NAME);
            msgObj.put("function","tast");
            msgObj.put("args", argsArray);
            connection.send(msgObj.toString());
        }

        public static <TYPE_A, TYPE_B> void test (TYPE_A a, TYPE_B b) throws JSONException{
            JSONObject msgObj = new JSONObject();
            JSONArray argsArray = new JSONArray();
            TornadoServer.addArg(argsArray, a);
			TornadoServer.addArg(argsArray,b);
            msgObj.put("hub",HUB_NAME);
            msgObj.put("function","test");
            msgObj.put("args", argsArray);
            connection.send(msgObj.toString());
        }
    }
    public static class TestClass2 {
        public static final String HUB_NAME = "TestClass2";
        
        public static <TYPE_A, TYPE_B, TYPE_C> void tast (TYPE_A a, TYPE_B b, TYPE_C c) throws JSONException{
            JSONObject msgObj = new JSONObject();
            JSONArray argsArray = new JSONArray();
            TornadoServer.addArg(argsArray, a);
			TornadoServer.addArg(argsArray, b);
			TornadoServer.addArg(argsArray,c);
            msgObj.put("hub",HUB_NAME);
            msgObj.put("function","tast");
            msgObj.put("args", argsArray);
            connection.send(msgObj.toString());
        }

        public static <TYPE_A, TYPE_B> void test (TYPE_A a, TYPE_B b) throws JSONException{
            JSONObject msgObj = new JSONObject();
            JSONArray argsArray = new JSONArray();
            TornadoServer.addArg(argsArray, a);
			TornadoServer.addArg(argsArray,b);
            msgObj.put("hub",HUB_NAME);
            msgObj.put("function","test");
            msgObj.put("args", argsArray);
            connection.send(msgObj.toString());
        }
    }
}
    