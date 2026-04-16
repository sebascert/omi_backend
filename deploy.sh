#!/usr/bin/env bash

set -euxo pipefail

# git clone https://github.com/sebascert/omi_backend.git

# dependencies

sudo dnf install -y java-17-openjdk-headless maven unzip
java --version

# oracle linux firewall config

sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

DOTENV="config/.env"
WALLET="config/wallet.zip"
SERVICE_CONFIG="config/omi_backend.service"

JAR="target/omi-backend-0.0.1-SNAPSHOT.jar"

SERVICE="omi_backend"
OPT_DIR="/opt/omi_backend"

if [ ! -f "$DOTENV" ]; then
    echo "Missing $DOTENV"
    exit 2
fi

if [ ! -f "$WALLET" ]; then
    echo "Missing $WALLET"
    exit 2
fi

# build and deploy

mvn clean package

sudo mkdir -p "$OPT_DIR"
sudo cp "$JAR" "$OPT_DIR/app.jar"
sudo cp "$DOTENV" "$OPT_DIR/.env"
sudo unzip -o "$WALLET" -d "$OPT_DIR/wallet"
sudo chown -R opc:opc "$OPT_DIR"

sudo cp "$SERVICE_CONFIG" "/etc/systemd/system/${SERVICE}.service"

sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE"
sudo systemctl restart "$SERVICE"
sudo systemctl status "$SERVICE" --no-pager
