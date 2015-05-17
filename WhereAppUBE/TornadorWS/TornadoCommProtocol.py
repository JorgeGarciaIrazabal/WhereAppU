import json
import logging
import sys
import tornado
import tornado.websocket
from HubDecorator import HubDecorator,HUBs_DICT
import inspect

__author__ = 'jgarc'

log = logging.getLogger(__name__)
log.debug("starting...")

class WebSocketFunctionHandler(tornado.websocket.WebSocketHandler):
    clients = {}

    def open(self, *args):
        self.ID = int(args[0])
        log.debug("open new connection with ID: %d "% self.ID)
        self.clients[self.ID] = self

    def on_message(self, message):
        log.debug("message from %s:/n%s" %(self.ID, message))
        try:
            msgObj = json.loads(message)
        except Exception as e:
            log.error(str(e))
        log.debug(message)
        cls = HUBs_DICT[msgObj["hub"]]
        args = msgObj["args"]
        args.insert(0,self)
        getattr(cls,msgObj["function"])(*msgObj["args"])

    def on_close(self):
        log.debug("client closed %s"%self.__dict__.get("ID","None"))
        if self.ID in self.clients.keys():
            self.clients.pop(self.ID)

    def check_origin(self, origin):
        return True

    def __getattr__(self, item):
        def clientFunction(*args):
            hubName = self.__getHubName()
            message = {"function":item,"args":list(args), "hub": hubName}
            self.write_message(json.dumps(message))

        return clientFunction

    @staticmethod
    def __getHubName(): #todo, try to optimize checking only Hub classes
        frame = inspect.currentframe().f_back.f_back
        code = frame.f_code
        name = code.co_name
        for className, obj in frame.f_globals.items():
            try:
                func = obj.__dict__[name]
                func_code =func.func_code if sys.version_info[0] == 2 else func.__code__
                assert func_code is code
            except Exception as e:
                pass
            else: # obj is the class that defines our method
                return className