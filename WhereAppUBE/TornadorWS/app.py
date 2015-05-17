from tornado import websocket, web, ioloop
import logging
import logging.config
import json
from HubDecorator import HubDecorator

logging.config.dictConfig(json.load(open('logging.json')))

from TornadoCommProtocol import WebSocketFunctionHandler

cl = []

class IndexHandler(web.RequestHandler):
    def get(self):
        self.render("index.html")

class SocketHandler(websocket.WebSocketHandler):
    def check_origin(self, origin):
        return True

    def open(self):
        if self not in cl:
            cl.append(self)

    def on_close(self):
        if self in cl:
            cl.remove(self)


app = web.Application([
    (r'/', IndexHandler),
    (r'/ws/(.*)', WebSocketFunctionHandler),
])

if __name__ == '__main__':
    @HubDecorator
    class TestClass2:

        def test(self, _client, a=1, b=2):
            print(a, b)

        def tast(self, _client, a=5, b=1, c=3):
            print(a, b)

    @HubDecorator
    class TestClass:

        def test(self, _client, a=1, b=2):
            print(a, b)

        def tast(self, _client, a=5, b=1, c=3):
            """
            @type _client: WebSocketFunctionHandler
            """
            _client.sendInfo(_client.ID)
            print(a, b)

    app.listen(8888)
    ioloop.IOLoop.instance().start()