#/bin/bash
PORT=${1:-8081}
while true; do sleep 1; curl http://localhost:$PORT/shop/1; echo -e; done
