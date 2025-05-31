<img src="images/blocklogger.png" style="width: 100%; height: 800px;">


# BlockLogger :bar_chart: :world_map:

[![GitHub license](https://img.shields.io/github/license/adiccsek/BlockLogger)](https://github.com/adiccsek/BlockLogger)
![GitHub release](https://img.shields.io/github/v/release/adiccsek/BlockLogger/)
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

messages:
  rollback:
    no-permission-error: "&cYou lack permission for /rollback"
    arguments-error: "&cUsage: /rollback <name> <minutes>"
    name-error: "&cPlayer not found!"
  
  locate:
    no-permission-error: "&cYou cannot use /locate"
    arguments-error: "&cUsage: /locate <radius> [player]"
    data-error: "&cNo logs found in area"
    locate-correct: "&7%player% &f%type% &a%block% &eat &7(%x%, %y%, %z%)"

debug-messages: false # Enable for troubleshooting