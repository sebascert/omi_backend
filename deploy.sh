#!/usr/bin/env bash

set -euxo pipefail

# git clone https://github.com/sebascert/omi_backend.git

# dependencies

sudo dnf install -y java-17-openjdk-headless maven unzip
java --version || {
    echo "Unable to install jdk"
    exit 1
}

# oracle linux firewall config
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-ports

# build and deploy

DOTENV="config/.env"
WALLET="config/wallet.zip"
SERVICE_CONFIG="config/omi_backend.service"
JAR="target/omi-backend-0.0.1-SNAPSHOT.jar"

if [ ! -f "$DOTENV" ];then
    echo "Missing $DOTENV"
    exit 2
fi

if [ ! -f "$WALLET" ];then
    echo "Missing $WALLET"
    exit 2
fi

mvn clean package

OPT_DIR="/opt/omi_backend"

sudo mkdir -p "$OPT_DIR"
sudo cp "$JAR" "$OPT_DIR"/app.jar
sudo cp "$DOTENV" "$OPT_DIR"/.env
sudo unzip -o "$WALLET" -d /opt/omi_backend/wallet
sudo chown -R opc:opc "$OPT_DIR"

sudo cp "$SERVICE_CONFIG" /etc/systemd/system/omi_backend.service

SERVICE="omi_backend"

sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE"
sudo systemctl start "$SERVICE"
sudo systemctl status "$SERVICE"
