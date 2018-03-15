import SimpleHTTPServer
from SimpleHTTPServer import BaseHTTPServer
from SimpleHTTPServer import SimpleHTTPRequestHandler
import SocketServer
import hashlib
import json
import urlparse

MAX_CHARS = 128
SHORT_URL_CACHE = dict()


PORT = 8082

class Testhandler(SimpleHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        print "got get request %s" % (self.path)
        if self.path == '/':
          self.path = '/index.html'
          return SimpleHTTPRequestHandler.do_GET(self)

    def do_POST(self):
        print "got post!!"
        content_len = int(self.headers.getheader('content-length', 0))
        post_body = self.rfile.read(content_len)
        test_data = json.loads(post_body)
        print "post_body(%s)" % (test_data)

        # validate
        parsed = urlparse.urlparse(test_data['url'])
        valid = all([parsed.scheme, parsed.netloc])
        if not valid:
            self.send_response(400)
            self.wfile.write(json.dumps(dict(error='invalid url')))
        else:
            shortened = url_shortener(test_data['url'])
            self.wfile.write(json.dumps(dict(shortened_url=shortened)))


httpd = SocketServer.TCPServer(("", PORT), Testhandler)

def get_shortened(shortened_url):
    return SHORT_URL_CACHE.get(shortened_url, None)

def url_shortener(url):
    shortened_url = None
    for i in range(1, MAX_CHARS):
        shortened_url = hashlib.sha512(url).hexdigest()[:i]
        if not get_shortened(shortened_url):
            break

    print SHORT_URL_CACHE
    SHORT_URL_CACHE[shortened_url] = url
    return shortened_url


if __name__ == '__main__':
    print "serving at port", PORT
    httpd.serve_forever()

