from unittest import TestCase
import mock

from src import example

class HandlerTest(TestCase):
    def test_unique(self):
        self.assertNotEqual(example.url_shortener('a234g'), example.url_shortener('absdfbsdf'))
