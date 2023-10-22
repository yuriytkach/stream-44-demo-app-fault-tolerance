#/bin/bash
PORT=${1:-8082}
while true; do sleep 1; curl http://localhost:$PORT/pr/1; echo -e; done
