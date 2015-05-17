__author__ = 'jgarc'

class HubDecoratorConfig:
    class Template():
        def __init__(self):
            self.class_ = None
            self.function = None
            self.argsCook = None

    JS_WRAPPER = """var $tornado
function $tornadoInit(args){{
    args = args || "";
    $tornado = new WebSocket('ws://localhost:8888/ws/'+args);
    {main}
}}
    """

    JS_CLASS_TEMPLATE = """
    $tornado.{name} = {{
        __HUB_NAME : "{name}",
        {functions}
    }}
    $tornado.{name}.client = {{}}"""
    JS_FUNCTION_TEMPLATE = """
        {name} : function ({args}){{
            {cook}
            body = {{"hub":this.__HUB_NAME, "function":"{name}","args":[]}};
            for(var i =0; i<arguments.length;i++)
                body.args.push(arguments[i])
            $tornado.send(JSON.stringify(body));
        }}"""
    JS_ARGS_COOK_TEMPLATE = "arguments[{iter}] = {name} || {default};"


    JAVA_CLASS_TEMPLATE = """
    public static class {name} {{
        public static final String HUB_NAME = "{name}";
        {functions}
    }}"""
    JAVA_FUNCTION_TEMPLATE = """
        public static {types} void {name} ({args}) throws JSONException{{
            JSONObject msgObj = new JSONObject();
            JSONArray argsArray = new JSONArray();
            {cook}
            msgObj.put("hub",HUB_NAME);
            msgObj.put("function","{name}");
            msgObj.put("args", argsArray);
            connection.send(msgObj.toString());
        }}"""
    JAVA_ARGS_COOK_TEMPLATE = "TornadoServer.addArg(argsArray,{arg});"

    JAVA_WRAPPER = """package %s;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class TornadoServer {{
    private static Gson gson = new Gson();
    private static TornadoConnection connection;

    public static void init(String uriStr) throws URISyntaxException {{
        connection = new TornadoConnection(uriStr);
        connection.connect();
    }}
    private static <TYPE_ARG> void addArg(JSONArray argsArray, TYPE_ARG arg) throws JSONException {{
        try {{
            argsArray.put(arg);
        }} catch (Exception e) {{
            argsArray.put(new JSONObject(gson.toJson(arg)));
        }}
    }}
    {main}
}}
    """

    @staticmethod
    def getJSTemplates():
        templates = HubDecoratorConfig.Template()
        templates.class_ = HubDecoratorConfig.JS_CLASS_TEMPLATE
        templates.function = HubDecoratorConfig.JS_FUNCTION_TEMPLATE
        templates.argsCook = HubDecoratorConfig.JS_ARGS_COOK_TEMPLATE
        return templates

    @staticmethod
    def getJAVATemplates():
        templates = HubDecoratorConfig.Template()
        templates.class_ = HubDecoratorConfig.JAVA_CLASS_TEMPLATE
        templates.function = HubDecoratorConfig.JAVA_FUNCTION_TEMPLATE
        templates.argsCook = HubDecoratorConfig.JAVA_ARGS_COOK_TEMPLATE
        return templates

    def __init__(self):
        pass

