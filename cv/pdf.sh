#!/bin/bash

# pandoc cv.md -o cv.pdf \
#   --pdf-engine=xelatex \
#   --template eisvogel.latex \
#   --syntax-highlighting=idiomatic

docker-compose up

exec open jose-alban.pdf