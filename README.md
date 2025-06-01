<img src="images/blocklogger.png" style="width: 100%; height: 800px;">


# BlockLogger :bar_chart: :world_map:

![GitHub release (latest by date)](https://img.shields.io/github/v/release/adiccsek/BlockLogger)
![GitHub downloads](https://img.shields.io/github/downloads/adiccsek/BlockLogger/total)
![Paper Version](https://img.shields.io/badge/Paper-1.19%2B-blueviolet)

Advanced block activity tracking and management system for PaperMC servers (1.19+)

## Features :sparkles:

- **Precision Tracking**: Records every block place/break with timestamps
- **Radius-Based Location**: Find changes within specific areas
- **Time-Travel Rollback**: Undo player actions within minute precision
- **Holographic Markers**: Visual indicators for located blocks
- **Secure MySQL Logging**: External database storage
- **Debug Mode**: Toggle detailed logging in config

## Installation :package:

1. Download from [GitHub Releases](https://github.com/adiccsek/BlockLogger/releases)
2. Place in `plugins/` folder
3. Restart server (auto-generates config)
4. Configure MySQL in `config.yml`

## Commands :keyboard:

### Staff Commands
| Command | Usage | Description | Permission |
|---------|-------|-------------|------------|
| `/rollback , /rb`  | `<name> <minutes>` | Revert player's block changes | `rollback-command` |
| `/locate` | `<radius> [player]` | Find block changes in area | `locate-command` |
| `/write` | - | Export logs to text file | OP only |

## Configuration :wrench:

```yaml
# config.yml (auto-generated)
database:
  host: "144.76.114.189" # Change this!
  username: "adiccsek" # Change this!
  password: "" # Change this!
  databaseName: "logger" # Change this!

debug-messages: false # Enable for troubleshooting
```
```yaml
# messages.yml (auto-generated)
messages:
  rollback:
    no-permission-error: "You do not have permission to use /rollback %player%."
    arguments-error: "Please use the correct number of arguments."
    usage-error: "The correct usage is: /rollback <name> <time>."
    name-error: "The name does not match!"
    give-item-correct: "You have added %block% to the inventory of: %player%".
    take-partially-correct: "Only Partially removed %amount% + %material%."
    take-correct: "You have taken %amount% %material% from %player%."
  locate:
    no-permission-error: "You do not have permission to use /locate %player%."
    arguments-error: "Please use the correct number of arguments."
    data-error: "No log data was found."
    locate-correct: "%player% %type% %block% at (%x%, %y%, %z%)"
    radius-error: "Radius must be a number."
  db:
    connection-true: "Connected to the database!" #When the database connects successfully (console)
    tables-true: "Created all the tables!" #When the tables are successfully created. (console)
    connection-false: "Connection is null or closed!" #When the connection does not exists. (console)
  write:
    write-true: "You have written the database to a statistics.txt file."
```