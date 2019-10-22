import falcon
import yaml

class QuoteResource:

    def on_get(self, req, resp):
        """Handles GET requests"""
        quote = {
            'quote': (
                "I've always been more interested in "
                "the future than in the past."
            ),
            'author': 'Grace Hopper'
        }

        # resp.media = quote
        resp.media = get_config()


def get_config():
    with open("config.yaml", 'r') as stream:
        return yaml.safe_load(stream)

app = falcon.API()
service = QuoteResource()
app.add_route('/quote', service)
