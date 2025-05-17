# LifeCore v1.0

A LifeSteal plugin for Minecraft 1.21.4 that introduces heart-based gameplay mechanics, automatic bans, and an in-game economy system. Manage player hearts, track suicides, and enable players to buy hearts or unban themselves via a GUI shop.

---

## Features ✨
- **Heart System**: Gain/lose hearts on kills. Admins can add/remove hearts via commands.
- **Automatic Bans**: 
  - Ban players after `3` suicides (configurable).
  - Temporary ban on death (prevents movement, limits hearts to 1).
- **GUI Shop**: Buy hearts (50k) or unban (150k) using in-game currency.
- **Economy Integration**: Requires Vault for transactions.
- **Placeholders**: Support for PlaceholderAPI (see list below).
- **Customizable**: Configure messages, prices, ban times, and more in `config.yml`.

---

## Installation 📥
1. Download the latest `LifeCore.jar` from releases.
2. Place it in your server's `plugins/` folder.
3. Restart the server.
4. (Optional) Install [Vault](https://www.spigotmc.org/resources/vault.34315/) for economy support and [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholders.

---

## Commands 🛠️

### Player Commands
| Command | Description |
|---------|-------------|
| `/lifecore` or `/lc` | Show available commands. |
| `/lifecore buy` | Open GUI shop to buy hearts or unban. |

### Admin Commands
| Command | Description |
|---------|-------------|
| `/lifecoreadmin` or `/lca` | Show admin commands. |
| `/lifecoreadmin heart add [player] [amount]` | Add hearts to a player. |
| `/lifecoreadmin heart remove [player] [amount]` | Remove hearts from a player. |
| `/lifecoreadmin heart list [player/all]` | List hearts of a player/all players. |

---

## Placeholders 📌
Requires [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

| Placeholder | Description |
|-------------|-------------|
| `%lifecore_hearts%` | Current hearts of the player. |
| `%lifecore_max_hearts%` | Maximum hearts allowed (configurable). |
| `%lifecore_is_banned%` | `true`/`false` if the player is banned. |
| `%lifecore_ban_time%` | Remaining ban time (0 if not banned). |
| `%lifecore_suicide_count%` | Number of suicides by the player. |
| `%lifecore_remaining_suicides%` | Suicides left before ban. |

---

## Configuration ⚙️
Edit `plugins/LifeCore/config.yml` to customize:

### Settings // [config.yml](https://github.com/BinaryCodee/LifeCore/blob/main/src/main/resources/config.yml)
```yaml
messages:
  prefix: "&8[&c&lLifeCore&8] &r"
  no_permission: "&cNon hai il permesso."
  player_not_found: "&cGiocatore non trovato."
  invalid_amount: "&cSpecifica un valore valido."
  heart_added: "&aHai aggiunto con successo &c{amount} cuore(i) &aa &f{player}&a."
  heart_removed: "&aHai rimosso con successo &c{amount} cuore(i) &aa &f{player}&a."
  heart_list_player: "&f{player} &aha &c{hearts} cuore(i)&a."
  heart_list_header: "&4&lCUORI &c&lGIOCATORI"
  heart_list_format: "&8* &f{player} &c{hearts} cuore(i)"
  heart_list_footer: ""
  killer_heart_gained: "&aHai guadagnato &c1 cuore &auccidendo &f{victim}&a!"
  victim_heart_lost: "&cHai perso &c1 cuore &cper essere stato ucciso da &f{killer}&c!"
  suicide_warning: "&cAttenzione: Se ti uccidi altre {remaining} volte, verrai bannato!"
  suicide_banned: "&cSei stato bannato per esserti ucciso troppe volte da solo!"
  heart_bought: "&aHai acquistato con successo &c1 cuore&a!"
  heart_redeemed: "&aHai riscattato con successo &c1 cuore&a!"
  not_enough_money: "&cNon hai abbastanza soldi!"
  banned_message: "&cSei stato bannato dal server per {time}!\n&ePuoi sbannarti usando &6/lifecore buy"
  unbanned_message: "&aSei stato unbannato dalla lifesteal!"
  unban_bought: "&aHai acquistato con successo l'unban! Ora puoi tornare a giocare."
  no_hearts_remaining: "&cNon hai più cuori e sei stato bannato!"
  max_hearts_reached: "&cHai raggiunto il numero massimo di cuori!"

settings:
  max_hearts: 20
  default_hearts: 10
  suicide_ban_count: 3
  suicide_ban_time: 7d
  death_ban_time: 4d
  starting_hearts_after_unban: 6
  enable_economy: true

economy:
  heart_price: 50000
  unban_price: 150000

gui:
  buy_menu:
    title: "&8Lifesteal Menu"
    size: 27
    unban_item:
      name: "&b&lUnban Lifesteal"
      material: "PAPER"
      lore:
        - "&7Clicca per aprire il Menu UnBan."
        - "&7Costo: &6{unban_price}"
      slot: 12
    heart_item:
      name: "&c&lAcquista Cuori"
      material: "RED_DYE"
      lore:
        - "&7Clicca per acquistare un cuore."
        - "&7Costo: &6{heart_price}"
      slot: 14

  unban_menu:
    title: "&8Unban Menu"
    size: 27
    crystal_item:
      name: "&b&lUnban"
      material: "NETHER_STAR"
      lore:
        - "&7Clicca per acquistare l'unban."
        - "&7Costo: &6{unban_price}"
      slot: 13

commands:
  lifecore:
    permission: "lifecore.use"
    aliases:
      - lc
      - life
  lifecoreadmin:
    permission: "lifecoreadmin.use"
    aliases:
      - lca
      - lifeadmin
