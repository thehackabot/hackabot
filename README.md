# hackabot
A Hackabar bot for Telegram. It supports matching people based on emotions, playing slideshow karaoke and number guessing games and other fun things like beer machines.

## Getting started
```bash
# downloading the project
git clone github.com/gethackabot/hackabot
...
cd hackabot

# installing dependencies
mvn install

# setting up the environment
LOCALIZATION=src/main/res/localization.properties
WEB_PORT=80
BOT_TOKEN="my-private-token"
BOT_NAME="my-bot-name"
COGNITION_KEY="my-azure-cognition-key"
SLIDESHOW_PATH=src/main/res/karaoke/

# starting up the bot
mvn exec:java
```