test:
	pipenv run pytest

serve:
	pipenv run gunicorn "service:get_app()" --reload

curl:
	curl -vv localhost:8000/readings/1/2019-08-01/2019-08-10

build:
	docker build -t sensat .

docker-serve:
	docker run --rm -it -p 8000:8000 sensat
