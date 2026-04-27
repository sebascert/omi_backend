#!/usr/bin/env bash

set -euxo pipefail

docker compose down
docker image prune -f
