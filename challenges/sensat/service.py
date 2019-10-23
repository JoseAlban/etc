import falcon
import yaml
import logging
import sys
import re

LOGGER = logging.getLogger('sensat.' + __name__)
LOGGER.setLevel(logging.DEBUG)
LOG_HANDLER = logging.StreamHandler(sys.stdout)
LOG_HANDLER.setFormatter(logging.Formatter(
    '%(asctime)s - %(name)s - %(levelname)s - %(message)s'))
LOGGER.addHandler(LOG_HANDLER)


class ReadingsResource:

    def on_get(self, req, resp, box_id, from_date, to_date):
        """Returns a list of readings of a sensor box between two dates"""

        valid_date = re.compile(r'^\d\d\d\d-\d\d-\d\d$')
        if not valid_date.match(from_date):
            raise falcon.HTTPBadRequest(
                'Invalid Argument',
                'Bad date: {} - expected pattern: {}'.format(
                    from_date, valid_date.pattern)
            )
        if not valid_date.match(to_date):
            raise falcon.HTTPBadRequest(
                'Invalid Argument',
                'Bad date: {} - expected pattern: {}'.format(
                    to_date, valid_date.pattern)
            )

        readings = {
            'quote': (
                "I've always been more interested in "
                "the future than in the past."
            ),
            'author': 'Grace Hopper'
        }

        LOGGER.info("Readings for box: %s", box_id)
        resp.media = get_config()


def get_config():
    with open("config.yaml", 'r') as stream:
        return yaml.safe_load(stream)

app = falcon.API()
service = ReadingsResource()
app.add_route('/readings/{box_id}/{from_date}/{to_date}', service)
