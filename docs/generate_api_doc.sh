#!/bin/bash

npm install -g redoc-cli

echo "Start API and press enter when its done"
read nothing
curl http://localhost:8080/v2/api-docs > data.json

redoc-cli  bundle -o api-index.html data.json

rm data.json
npm uninstall -g redoc-cli

mv api-index.html ./api/index.html

