import service

from unittest.mock import MagicMock
import pytest
import falcon
from falcon import testing
import msgpack


@pytest.fixture
def mock_dbconn():
    return MagicMock()


@pytest.fixture
def client(mock_dbconn):
    api = service.create_app(mock_dbconn)
    return testing.TestClient(api)


def test_bad_date(client):
    response = client.simulate_get('/readings/1/1235/2019-08-10')

    assert response.status == falcon.HTTP_BAD_REQUEST


# TODO fix test
# def test_found_results(client, mock_dbconn):
#     mock_dbconn.cursor.return_value = cursor = MagicMock()
#     cursor.fetchall.return_value = [(1, 2, 3)]
#     response = client.simulate_get('/readings/1/2019-08-01/2019-08-10')
#     result_doc = msgpack.unpackb(response.content, raw=False)

#     assert result_doc == [{}]
#     assert response.status == falcon.HTTP_OK
