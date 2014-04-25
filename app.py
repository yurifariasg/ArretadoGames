from flask import Flask
server = Flask(__name__)

@server.route("/")
def index():
    everything = open('index.html').read()
    return everything

if __name__ == "__main__":
    server.run()
