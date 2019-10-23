import falcon
import yaml
import logging
import sys
import re
import pymysql

LOGGER = logging.getLogger('sensat.' + __name__)
LOGGER.setLevel(logging.DEBUG)
LOG_HANDLER = logging.StreamHandler(sys.stdout)
LOG_HANDLER.setFormatter(logging.Formatter(
    '%(asctime)s - %(name)s - %(levelname)s - %(message)s'))
LOGGER.addHandler(LOG_HANDLER)


def get_config():
    with open("config.yaml", 'r') as stream:
        return yaml.safe_load(stream)


def get_db_connection():
    # need to be careful not to leak connections
    return pymysql.connect(host=CONFIG['db']['url'],
                           user=CONFIG['db']['username'],
                           password=CONFIG['db']['password'],
                           db=CONFIG['db']['name'],)


CONFIG = get_config()
DB_CONNECTION = get_db_connection()


class ReadingsResource:

    def __init__(self, db_conn):
        self._db_conn = db_conn


    def on_get(self, req, resp, box_id, from_date, to_date):
        """Returns a list of readings of a sensor box between two dates"""

        LOGGER.info("Readings for box: %s", box_id)

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

        results = None
        with self._db_conn.cursor() as cursor:
            sql = """SELECT * FROM readings
                    INNER JOIN sensors ON readings.sensor_id=sensors.id
                    WHERE reading_ts BETWEEN %s AND %s"""
            LOGGER.info('SQL: %s', sql)
            cursor.execute(sql, (from_date, to_date,))
            results = cursor.fetchall()

        readings = None
        if results:
            LOGGER.info('Results found: %s', len(results))
            LOGGER.debug('Sample result: %s', results[0])
            readings = [
                {
                    'box_id': r[1],
                    'sensor_id': r[2],
                    'name': r[11],
                    'unit': r[6],
                    'reading': r[4],
                    'reading_ts': '{}'.format(r[3]),
                }
                for r in results
            ]
        else:
            raise falcon.HTTPNotFound()

        resp.media = readings


def create_app(db_conn):
    service = ReadingsResource(db_conn)
    api = falcon.API()
    api.add_route('/readings/{box_id}/{from_date}/{to_date}', service)
    return api


def get_app():
    return create_app(DB_CONNECTION)
