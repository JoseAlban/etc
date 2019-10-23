FROM python:3.7-slim-buster

# https://github.com/pypa/pipenv/issues/2834
COPY Pipfile /app/
COPY Pipfile.lock /app/

# for better caching, this first
WORKDIR /app/
RUN pip install pipenv
RUN pipenv sync

COPY config.yaml /app/
COPY service.py /app/

EXPOSE 8000
CMD ["pipenv", "run", "gunicorn", "--worker-tmp-dir", "/dev/shm", "service:get_app()"]
