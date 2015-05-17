var $tornado
function $tornadoInit(args){
    args = args || "";
    $tornado = new WebSocket('ws://localhost:8888/ws/'+args);
    
    $tornado.TestClass = {
        __HUB_NAME : "TestClass",
        
        tast : function (a, b, c){
            arguments[0] = a || 5;
			arguments[1] = b || 1;
			arguments[2] = c || 3;
            body = {"hub":this.__HUB_NAME, "function":"tast","args":[]};
            for(var i =0; i<arguments.length;i++)
                body.args.push(arguments[i])
            $tornado.send(JSON.stringify(body));
        },

        test : function (a, b){
            arguments[0] = a || 1;
			arguments[1] = b || 2;
            body = {"hub":this.__HUB_NAME, "function":"test","args":[]};
            for(var i =0; i<arguments.length;i++)
                body.args.push(arguments[i])
            $tornado.send(JSON.stringify(body));
        }
    }
    $tornado.TestClass.client = {}
    $tornado.TestClass2 = {
        __HUB_NAME : "TestClass2",
        
        tast : function (a, b, c){
            arguments[0] = a || 5;
			arguments[1] = b || 1;
			arguments[2] = c || 3;
            body = {"hub":this.__HUB_NAME, "function":"tast","args":[]};
            for(var i =0; i<arguments.length;i++)
                body.args.push(arguments[i])
            $tornado.send(JSON.stringify(body));
        },

        test : function (a, b){
            arguments[0] = a || 1;
			arguments[1] = b || 2;
            body = {"hub":this.__HUB_NAME, "function":"test","args":[]};
            for(var i =0; i<arguments.length;i++)
                body.args.push(arguments[i])
            $tornado.send(JSON.stringify(body));
        }
    }
    $tornado.TestClass2.client = {}
}
    