# LifeCore v1.2-NewFix

**LifeCore** is a feature-rich Minecraft plugin designed for **Lifesteal** gameplay, offering a robust heart-based system, advanced team management, custom shop, random teleport (RTP), and spawn mechanics. Built for Minecraft 1.21.x, it integrates with Vault for economy support and PlaceholderAPI for dynamic placeholders. With extensive customization options, LifeCore enhances survival and PvP servers with engaging mechanics for players and powerful tools for administrators.

---

## Features ✨

- **Lifesteal Heart System**  
  - Gain or lose hearts on kills, with configurable limits (default: 10 starting, 30 max).  
  - Automatic bans on death or excessive suicides (configurable duration).  
  - Purchase hearts or unbans via `/lifecore buy` using in-game currency (75,000 for hearts, 300,000 for unbans).

- **Custom Team System**  
  - Create and manage teams with commands like `/teams create`, `/teams invite`, `/teams chat [message]`, and more.  
  - Features team homes (up to 5), ally management, promotions, and detailed stats tracking.  
  - Note: Teams from previous versions must be recreated due to the new system.

- **Custom Shop System**  
  - Interactive shop interface via `/shop` with categories (Farming, Mob Drops, Materials, Generals, Tools).  
  - Quick-sell GUI with `/sellgui`.  
  - **Left-click** to buy, **right-click** to sell items in the shop.  
  - Economy integration via Vault for seamless transactions.

- **Custom Spawn System**  
  - Set spawn points with `/setspawn` or `/lifecoreadmin setspawn`.  
  - Teleport to spawn using `/spawn` or `/lifecore spawn` (2-second delay, bypassable with permission).  

- **Random Teleport (RTP)**  
  - Safe random teleportation with `/rtp`, `/randomtp`, or `/wild` across Overworld, Nether, and End.  
  - Configurable radius, safe/unsafe blocks, and optional economy cost.

- **Additional Commands**  
  - `/stats`: View player statistics (kills, deaths, etc.).  
  - `/ping`: Check server latency.  
  - `/lifecoreadmin reload`: Reload configuration without restarting the server.

- **PlaceholderAPI Support**  
  - Extensive placeholders for hearts, team names, stats, and more (see below).  

- **Performance & Stability**  
  - Optimized for minimal server load with internal bug fixes and improvements.  
  - Compatible with Spigot, Paper, and Purpur (1.8.x - 1.21.x).

---

## Installation 📥

1. Download the latest `LifeCore.jar` (v1.2-NewFix) from the [releases page](https://github.com/BinaryCodee/LifeCore/releases).  
2. Place the `.jar` file in your server’s `plugins/` folder.  
3. Install **dependencies**:  
   - **Required**: [Vault](https://www.spigotmc.org/resources/vault.34315/) for economy support.  
   - **Optional**: [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholders.  
4. Start or restart your server to generate configuration files (`config.yml`, `teams.yml`, `shops.yml`).  
5. Customize settings in `plugins/LifeCore/`.  
6. Use `/lifecoreadmin reload` to apply changes without restarting.

---

## Commands 🛠️

### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/lifecore` or `/lc` | Show available commands | `lifecore.use` |
| `/lifecore buy` | Open GUI shop to buy hearts or unban | `lifecore.buy` |
| `/spawn` | Teleport to server spawn | `lifecore.spawn` |
| `/rtp` or `/randomtp` or `/wild` | Randomly teleport to a safe location | `lifecore.rtp.use` |
| `/shop` | Open the custom shop interface | `lifecore.shop` |
| `/sellgui` | Open the quick-sell GUI | `lifecore.sellgui` |
| `/stats` | View player statistics | `lifecore.stats` |
| `/ping` | Check server latency | `lifecore.ping` |
| `/teams <subcommand>` | Manage teams (e.g., create, invite, chat) | `lifecore.teams` |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/lifecoreadmin` or `/lca` | Show admin commands | `lifecoreadmin.use` |
| `/lifecoreadmin heart add [player] [amount]` | Add hearts to a player | `lifecoreadmin.heart.add` |
| `/lifecoreadmin heart remove [player] [amount]` | Remove hearts from a player | `lifecoreadmin.heart.remove` |
| `/lifecoreadmin heart list [player/all]` | List hearts of a player/all players | `lifecoreadmin.heart.list` |
| `/lifecoreadmin reload` | Reload configuration files | `lifecoreadmin.reload` |
| `/lifecoreadmin setspawn` | Set the server spawn point | `lifecoreadmin.setspawn` |
| `/lifecoreadmin ban [player] [time]` | Ban a player for a specified time | `lifecoreadmin.ban` |
| `/lifecoreadmin unban [player]` | Unban a player | `lifecoreadmin.unban` |
| `/teamsadmin <subcommand>` | Admin team management (e.g., kick, reset) | `lifecoreadmin.teams` |

### Bypass Permissions
| Permission | Description |
|------------|-------------|
| `lifecore.spawn.bypass` | Bypass the 2-second spawn teleport delay |

---

## Placeholders 📌
Requires [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

### Lifesteal Placeholders
| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%lifecore_hearts%` | Current number of hearts for the player | `10` |
| `%lifecore_max_hearts%` | Maximum number of hearts allowed | `30` |
| `%lifecore_is_banned%` | Whether the player is banned (`true`/`false`) | `false` |
| `%lifecore_ban_time%` | Remaining ban time in seconds (`0` if not banned) | `3600` or `0` |
| `%lifecore_suicide_count%` | Number of suicides by the player | `2` |
| `%lifecore_remaining_suicides%` | Suicides left before ban | `1` |
| `%lifecore_heart_price%` | Price to purchase a heart (Italian locale) | `75.000` |
| `%lifecore_unban_price%` | Price to purchase an unban (Italian locale) | `300.000` |
| `%lifecore_balance%` | Player’s economy balance (requires Vault) | `75.000` |
| `%lifecore_enough_heart%` | If player can afford a heart (clickable text) | `§a✓ §7Clicca per acquistare un cuore!` |
| `%lifecore_enough_unban%` | If player can afford an unban (clickable text) | `§a✓ §7Clicca per acquistare l'unban!` |

### Team Placeholders
| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%lifecore_teams_name%` | Name of the player’s team (empty if none) | `Warriors` or `` |
| `%lifecore_teams_members%` | Number of members in the player’s team | `5` or `0` |
| `%lifecore_teams_staff%` | Number of staff (leader, admin, mod) in the team | `2` or `0` |
| `%lifecore_teams_online%` | Number of online members in the team | `3` or `0` |
| `%lifecore_teams_offline%` | Number of offline members in the team | `2` or `0` |
| `%lifecore_teams_position%` | Team’s leaderboard position (based on kills) | `1` or `0` |
| `%lifecore_teams_ally_name%` | Name of the first allied team (empty if none) | `Guardians` or `` |
| `%lifecore_teams_ally_members%` | Total members in allied teams | `8` or `0` |
| `%lifecore_teams_ally_staff%` | Total staff in allied teams | `3` or `0` |
| `%lifecore_teams_ally_online%` | Total online members in allied teams | `5` or `0` |
| `%lifecore_teams_ally_offline%` | Total offline members in allied teams | `3` or `0` |
| `%lifecore_teams_ally_position%` | Leaderboard position of the first allied team | `2` or `0` |

---

## Configuration ⚙️
Edit the configuration files in `plugins/LifeCore/` to customize the plugin:

- **config.yml**: Main settings for messages, economy, hearts, bans, teams, shop, and RTP.  
- **teams.yml**: Stores team data (members, homes, allies).  
- **shops.yml**: Defines shop categories, items, and prices.

### Config Version: v1.2-NewFix (config.yml)
<details>
<summary>Show Config</summary>

```yaml
messages:
  prefix: "&8[&c&lLifeCore&8] &r"
  no_permission: "&cNon hai il permesso."
  player_not_found: "&cGiocatore non trovato."
  player_only: "&cComando solo per i giocatori."
  invalid_usage: "&cUsa: %usage%"
  invalid_amount: "&cSpecifica un valore valido."
  heart_added: "&aHai aggiunto con successo &c%amount% cuore(i) &aa &f%player%&a."
  heart_removed: "&aHai rimosso con successo &c%amount% cuore(i) &aa &f%player%&a."
  heart_list_player: "&f%player% &aha &c%hearts% cuore(i)&a."
  heart_list_header: "&4&lCUORI &c&lGIOCATORI"
  heart_list_format: "&8* &f%player% &c%hearts% cuore(i)"
  heart_list_footer: ""
  hearts_limit_reached: "&cHai raggiunto il limite massimo di cuori (&e20&c)!"
  hearts_limit_reached_admin: "&cIl giocatore &e%player% &cha raggiunto il limite massimo di cuori (&e%max_hearts%&c)!"
  heart_gained: "&aHai guadagnato &c%hearts% cuore(i) &auccidendo &f%victim%&a!"
  heart_lost: "&cHai perso &c%hearts% cuore(i) &cper essere stato ucciso da &f%killer%&c!"
  heart_lost_suicide: "&cHai perso &c%hearts% cuore(i) &cper esserti suicidato!"
  shop_buy_success: "&aHai acquistato %quantity% %item% per &a$%price% &e!"
  shop_sell_success: "&aHai venduto %quantity% %item% per &a$%price% &e!"
  shop_not_enough_money: "&cNon hai abbastanza soldi."
  shop_not_enough_items: "&cNon hai abbastanza items da vendere."
  shop_inventory_full: "&cIl tuo inventario è pieno."
  shop_invalid_quantity: "&cSeleziona una quantità valida."
  shop_sell_gui_title: "&c&lVendi Oggetti"
  shop_item_not_sellable: "&cQuesto oggetto non può essere venduto!"
  suicide_warning: "&cAttenzione: Se ti uccidi altre %remaining% volte, verrai bannato!"
  suicide_banned: "&cSei stato bannato per esserti ucciso troppe volte da solo!"
  heart_bought: "&aHai acquistato con successo &c1 cuore&a!"
  heart_redeemed: "&aHai riscattato con successo &c1 cuore&a!"
  not_enough_money: "&cNon hai abbastanza soldi!"
  ban_command_success: "&aIl giocatore %player% è stato bannato per %time%."
  unban_command_success: "&aIl giocatore %player% è stato unbannato."
  banned_message: "&cSei stato bannato dal server per %time%!\n&ePuoi sbannarti usando &6/lifecore buy"
  banned_join_message: "&cSei ancora bannato dal server per %time%!\n&ePuoi sbannarti usando &6/lifecore buy"
  banned_title: "&c&lSEI BANNATO"
  banned_subtitle: "&eTempo rimanente: &6%time%"
  banned_freeze_message: "&cNon puoi muoverti mentre sei bannato! Usa: /lifecore buy e acquista un cuore o l'unban!"
  banned_command_blocked: "&c&lSei stato bannato! &7Puoi usare solo: &e&n/lifecore buy&7 per acquistare dei cuori o l'unban&e!"
  banned_chat_format: "&8[&c&lBANNATO&8] &7%player%: &8%message%"
  banned_heart_redeem_blocked: "&cNon puoi riscattare cuori mentre sei bannato!"
  ban_death: "&cHai perso tutti i tuoi cuori e sei stato bannato per %time%!"
  unbanned_message: "&aSei stato unbannato dalla lifesteal!"
  unban_bought: "&aHai acquistato con successo l'unban! Ora puoi tornare a giocare."
  already_unbanned: "&cNon puoi acquistare l'UnBan perché non sei bannato!"
  no_hearts_remaining: "&cNon hai più cuori e sei stato bannato!"
  max_hearts_reached: "&cHai raggiunto il numero massimo di cuori!"
  inventory_full: "&cIl tuo inventario è pieno."
  spawn_set: "&aHai settato lo spawn!"
  spawn_not_set: "&cLo spawn del server non esiste."
  teleport_countdown_started: "&aVerrai teletrasportato allo spawn in &e%seconds% secondi&a. Non muoverti!"
  teleport_countdown_title: "&a&lTELEPORT IN &e&l%seconds%"
  teleport_countdown_subtitle: "&7Non muoverti!"
  teleport_cancelled_movement: "&cTeleport annullato. Ti sei mosso!"
  teleported_to_spawn: "&aSei tornato allo spawn."
  teleport_complete_title: "&a&lSPAWN"
  teleport_complete_subtitle: "&bSei tornato allo spawn&e!"
  rtp_searching: "&b&l✦ &f&lRTP &8▸ &7Sto cercando una posizione sicura... &b✦"
  rtp_failed: "&c&l✖ &f&lRTP &8▸ &cNon sono riuscito a trovare una posizione sicura. Riprova più tardi."
  rtp_failed_title: "&c&lRTP FALLITO"
  rtp_failed_subtitle: "&7Nessuna posizione sicura trovata!"
  rtp_success: "&a&l✓ &f&lRTP &8▸ &aTeletrasportato a &e%x%&7, &e%y%&7, &e%z% &anel mondo &b%world%&a!"
  rtp_success_title: "&a&lRTP COMPLETATO"
  rtp_success_subtitle: "&b%world% &7(%x%, %y%, %z%)"
  rtp_cooldown: "&e&l⏱ &f&lRTP &8▸ &cDevi aspettare ancora &e%time% secondi &cprima di usare l'RTP."
  rtp_world_not_found: "&c&l✖ &f&lRTP &8▸ &cIl mondo '&e%world%&c' non è stato trovato."
  rtp_not_enough_money: "&c&l$ &f&lRTP &8▸ &cNon hai abbastanza soldi. Costo: &e%cost%&c."
  rtp_money_taken: "&a&l$ &f&lRTP &8▸ &aHai pagato &e%cost% &aper l'RTP."
  rtp_title: "&b&lTeletrasporto in %time%..."
  rtp_countdown: "&e&l⟳ &f&lRTP &8▸ &7Teletrasporto in &e%time% secondi&7..."
  rtp_countdown_subtitle: "&7Non muoverti!"
  help_header: "&c&lLIFESTEAL &7- &e&lCOMANDI UTENTI"
  help_command: "&c/lifecore help &8- &7Mostra tutti i comandi per gli utenti"
  buy_command: "&c/lifecore buy &8- &7Apri il menu della lifesteal"
  spawn_command: "&c/spawn &8- &7Vai allo spawn del server"
  stats_command: "&c/stats [player] &8- &7Mostra le statistiche di un giocatore"
  stats_self: "&a&lLe tue Statistiche:\n&7Cuori: &c%hearts%\n&7Uccisioni: &e%kills%\n&7Morti: &e%deaths%\n&7Soldi: &a$%balance%"
  stats_other: "&a&lStatistiche di %player%:\n&7Cuori: &c%hearts%\n&7Uccisioni: &e%kills%\n&7Morti: &e%deaths%\n&7Soldi: &a$%balance%"
  teams_already_in_team: "&cSei già in un team!"
  teams_not_in_team: "&cNon sei in nessun team!"
  teams_name_taken: "&cQuesto nome del team è già in uso!"
  teams_created: "&aTeam %team% creato con successo!"
  teams_disbanded: "&cIl team %team% è stato sciolto!"
  teams_no_permission: "&cNon hai il permesso per eseguire questa azione!"
  teams_invite_received: "&aHai ricevuto un invito per unirti al team %team% da %player%!"
  teams_invite_sent: "&aInvito inviato a %player%!"
  teams_no_invite: "&cNon hai nessun invito in sospeso!"
  teams_invite_denied: "&aHai rifiutato l'invito al team!"
  teams_joined: "&a%player% si è unito al team %team%!"
  teams_invalid_rank: "&cRank non valido! Usa: ADMIN, MOD, MEMBER"
  teams_no_leader_promote: "&cNon puoi promuovere a LEADER!"
  teams_no_leader_demote: "&cNon puoi retrocedere a LEADER!"
  teams_promoted: "&a%player% è stato promosso a %rank%!"
  teams_demoted: "&a%player% è stato retrocesso a %rank%!"
  teams_no_kick_leader: "&cNon puoi espellere il leader!"
  teams_kicked: "&c%player% è stato espulso dal team!"
  teams_kicked_player: "&cSei stato espulso dal team %team%!"
  teams_max_homes: "&cIl team ha raggiunto il limite massimo di case (%max%)!"
  teams_home_set: "&aCasa %home% impostata con successo!"
  teams_home_deleted: "&aCasa %home% eliminata con successo!"
  teams_home_not_found: "&cCasa non trovata!"
  teams_home_teleported: "&aTeletrasportato alla casa %home%!"
  teams_no_homes: "&cIl team non ha case impostate!"
  teams_homes_list: "&aElenco delle case del team:"
  teams_name_changed: "&aIl nome del team è stato cambiato in %team%!"
  teams_not_found: "&cTeam non trovato!"
  teams_info: "&aTeam: %team% | Membri: %members% | Staff: %staff% | Online: %online% | Offline: %offline%"
  teams_stats: "&aStatistiche di %team%: Uccisioni: %kills% | Morti: %deaths% | Serie: %streak%"
  teams_top_header: "&aClassifica dei migliori team:"
  teams_top_format: "&7%position%. %team% - Uccisioni: %kills%"
  teams_position: "&aPosizione di %team%: %position%"
  teams_already_allied: "&cSei già alleato con questo team!"
  teams_ally_request: "&aRichiesta di alleanza ricevuta da %team%!"
  teams_ally_request_sent: "&aRichiesta di alleanza inviata a %team%!"
  teams_no_ally_invite: "&cNessuna richiesta di alleanza in sospeso!"
  teams_ally_accepted: "&aAlleanza accettata con %team%!"
  teams_ally_denied: "&aRichiesta di alleanza con %team% rifiutata!"
  teams_not_allied: "&cNon sei alleato con questo team!"
  teams_ally_removed: "&cAlleanza con %team% rimossa!"
  teams_ally_stats: "&aStatistiche alleato %team%: Uccisioni: %kills% | Morti: %deaths% | Serie: %streak%"
  teams_ally_position: "&aPosizione alleato %team%: %position%"
  teams_chat_enabled: "&aChat del team attivata!"
  teams_chat_disabled: "&aChat del team disattivata!"
  teams_ally_chat_enabled: "&aChat degli alleati attivata!"
  teams_ally_chat_disabled: "&aChat degli alleati disattivata!"
  teams_chat_format: "&7[Team] %player%: %message%"
  teams_ally_chat_format: "&7[Ally] %team% %player%: %message%"
  teams_admin_reset: "&aTeam %team% resettato con successo!"
  teams_admin_stat_set: "&aStatistica %stat% di %team% impostata a %value%!"
  teams_help:
    - "&aComandi Team:"
    - "&7/teams help &8- Mostra questo messaggio"
    - "&7/teams info [team/giocatore] &8- Mostra informazioni sul team"
    - "&7/teams create [nome] &8- Crea un nuovo team"
    - "&7/teams delete &8- Scioglie il tuo team"
    - "&7/teams invite [giocatore] &8- Invita un giocatore"
    - "&7/teams accept &8- Accetta un invito"
    - "&7/teams deny &8- Rifiuta un invito"
    - "&7/teams chat [messaggio] &8- Attiva/disattiva chat team o invia messaggio"
    - "&7/teams promote [giocatore] [rank] &8- Promuovi un membro"
    - "&7/teams demote [giocatore] [rank] &8- Retrocedi un membro"
    - "&7/teams kick [giocatore] &8- Espelli un membro"
    - "&7/teams sethome [nome] &8- Imposta una casa del team"
    - "&7/teams delhome [nome] &8- Elimina una casa del team"
    - "&7/teams home [nome] &8- Teletrasportati a una casa"
    - "&7/teams listhome &8- Elenca le case del team"
    - "&7/teams stats &8- Mostra statistiche del team"
    - "&7/teams top &8- Mostra i migliori team"
    - "&7/teams position &8- Mostra la posizione del team"
    - "&7/teams ally add [team] &8- Richiedi un'alleanza"
    - "&7/teams ally accept &8- Accetta un'alleanza"
    - "&7/teams ally deny &8- Rifiuta un'alleanza"
    - "&7/teams ally remove [team] &8- Rimuovi un'alleanza"
    - "&7/teams ally stats [team] &8- Mostra statistiche alleato"
    - "&7/teams ally chat [messaggio] &8- Attiva/disattiva chat alleati o invia messaggio"
    - "&7/teams ally position [team] &8- Mostra posizione alleato"
  teams_admin_help:
    - "&aComandi Admin Team:"
    - "&7/teamsadmin help &8- Mostra questo messaggio"
    - "&7/teams create [nome] &8- Crea un team"
    - "&7/teams delete [name] &8- Scioglie un team"
    - "&7/teamsadmin kick [giocatore] [team] &8- Espelli un giocatore da un team"
    - "&7/teamsadmin reset [team] &8- Resetta un team"
    - "&7/teamsadmin ally add [team1] [team2] &8- Forza un'alleanza"
    - "&7/teamsadmin ally remove [team1] [team2] &8- Rimuovi un'alleanza"
    - "&7/teamsadmin setstat [team] [stat] [valore] &8- Imposta una statistica"
  ping_self: "&7Il tuo ping è: %ping_color%%ping%ms"
  ping_other: "&7Il ping di &e%player% &7è: %ping_color%%ping%ms"
  admin_help_command: "&c/lifecoreadmin &8- &7Mostra tutti i comandi per gli admin"
  admin_help_header: "&c&lLIFESTEAL &7- &6&lCOMANDI ADMIN"
  admin_help_heart_add: "&c/lifelongadmin heart add %player% %amount% &8- &7Aggiungi i cuori ad un giocatore"
  admin_help_heart_remove: "&c/lifecoreadmin heart remove %player% %amount% &8- &fRimuovi i cuori di un giocatore"
  admin_help_heart_list: "&c/lifecoreadmin heart list %player%/all &8- &7Lista cuori di un giocatore o di tutti i giocatori"
  admin_help_ban: "&c/lifecoreadmin ban %player% %time% &8- &7Banna un giocatore per un determinato tempo"
  admin_help_unban: "&c/lifecoreadmin unban %player% &8- &7Sbanna un giocatore"
  admin_help_setspawn: "&c/lifecoreadmin setspawn &8- &7Imposta lo spawn del server"
  admin_help_reload: "&c/lifecoreadmin reload &8- &7Ricarica la configurazione del plugin"
  admin_help_footer: "&a"
  reload_success: "&aHai ricaricato con successo il config.yml&e!"

settings:
  max_hearts: 30
  default_hearts: 10
  heart_transfer_count: 1
  suicide_heart_loss_enabled: 1
  suicide_ban_count: 3
  suicide_ban_time: "7d"
  death_ban_time: 4d
  starting_hearts_after_unban: 6
  enable_economy: true
  spawn_teleport_delay: 2
  spawn_teleport_title_enabled: true
  spawn_teleport_sound_enabled: true
  spawn_teleport_sound: "ENTITY_ENDERMAN_TELEPORT"
  spawn_teleport_sound_volume: 1.0
  spawn_teleport_sound_pitch: 1.0
  spawn_countdown_title_enabled: true
  spawn_countdown_sound_enabled: true
  spawn_countdown_sound: "BLOCK_NOTE_BLOCK_PLING"
  spawn_countdown_sound_volume: 1.0
  spawn_countdown_sound_pitch: 1.0
  ban_title_enabled: true
  ban_freeze_message_enabled: true
  ban-block-commands: true
  ban-chat-format: true

teams:
  enabled: true
  max_homes: 5
  max_members: 6
  friendly_fire: false
  chat_enabled: true
  ally_chat_enabled: true

shop:
  main:
    title: '&2&lSHOP'
    size: 36
    items:
      farming:
        slot: 11
        material: WHEAT
        name: '&6&lFarming'
      mob_drops:
        slot: 13
        material: ROTTEN_FLESH
        name: '&c&lMobs Drops'
      materials:
        slot: 15
        material: GOLD_INGOT
        name: '&3&lMateriali'
      generals:
        slot: 21
        material: LAVA_BUCKET
        name: '&b&lGenerali'
      tools:
        slot: 23
        material: DIAMOND_PICKAXE
        name: '&5&lTools'
  transaction:
    buy_title: "&2Acquista: &6%item%"
    sell_title: "&cVendi: &6%item%"
    confirm: "&aConferma"
    cancel: "&cAnnulla"
    quantity: "&eQuantità: %item%"
  categories:
    farming:
      size: 54
      items:
        wheat_seeds:
          slot: 10
          material: WHEAT_SEEDS
          buy: 10
          sell: 2.5
        wheat:
          slot: 11
          material: WHEAT
          buy: 64
          sell: 16
        carrot:
          slot: 12
          material: CARROT
          buy: 64
          sell: 16
        potato:
          slot: 13
          material: POTATO
          buy: 64
          sell: 16
        beetroot_seeds:
          slot: 14
          material: BEETROOT_SEEDS
          buy: 10
          sell: 2.5
        beetroot:
          slot: 15
          material: BEETROOT
          buy: 64
          sell: 16
        sweet_berries:
          slot: 16
          material: SWEET_BERRIES
          buy: 12
          sell: 3
        pumpkin_seeds:
          slot: 19
          material: PUMPKIN_SEEDS
          buy: 25
          sell: 5
        pumpkin:
          slot: 20
          material: PUMPKIN
          buy: 512
          sell: 320
        melon_seeds:
          slot: 21
          material: MELON_SEEDS
          buy: 25
          sell: 5
        melon:
          slot: 22
          material: MELON
          buy: 512
          sell: 192
        melon_slice:
          slot: 23
          material: MELON_SLICE
          buy: 64
          sell: 16
        bamboo:
          slot: 24
          material: BAMBOO
          buy: 12
          sell: 3
        sugar_cane:
          slot: 25
          material: SUGAR_CANE
          buy: 384
          sell: 128
        cactus:
          slot: 28
          material: CACTUS
          buy: 512
          sell: 256
        red_mushroom:
          slot: 29
          material: RED_MUSHROOM
          buy: 15
          sell: 3.75
        brown_mushroom:
          slot: 30
          material: BROWN_MUSHROOM
          buy: 15
          sell: 3.75
        nether_wart:
          slot: 31
          material: NETHER_WART
          buy: 12
          sell: 3
        cocoa_beans:
          slot: 32
          material: COCOA_BEANS
          buy: 10
          sell: 2.5
        apple:
          slot: 33
          material: APPLE
          buy: 15
          sell: 5
        poppy:
          slot: 34
          material: POPPY
          buy: 15
          sell: 3
    mob_drops:
      size: 54
      pages:
        '1':
          items:
            raw_beef:
              slot: 0
              material: BEEF
              buy: 30
              sell: 8
            steak:
              slot: 1
              material: COOKED_BEEF
              buy: 60
              sell: 8
            raw_porkchop:
              slot: 2
              material: PORKCHOP
              buy: 30
              sell: 8
            cooked_porkchop:
              slot: 3
              material: COOKED_PORKCHOP
              buy: 60
              sell: 4
            raw_mutton:
              slot: 4
              material: MUTTON
              buy: 30
              sell: 7
            cooked_mutton:
              slot: 5
              material: COOKED_MUTTON
              buy: 60
              sell: 15
            raw_chicken:
              slot: 6
              material: CHICKEN
              buy: 30
              sell: 5
            cooked_chicken:
              slot: 7
              material: COOKED_CHICKEN
              buy: 60
              sell: 5
            raw_rabbit:
              slot: 8
              material: RABBIT
              buy: 30
              sell: 8
            cooked_rabbit:
              slot: 9
              material: COOKED_RABBIT
              buy: 60
              sell: 16
            leather:
              slot: 10
              material: LEATHER
              buy: 40
              sell: 16
            feather:
              slot: 11
              material: FEATHER
              buy: 12
              sell: 2
            rotten_flesh:
              slot: 12
              material: ROTTEN_FLESH
              buy: 150
              sell: 40
            bone:
              slot: 13
              material: BONE
              buy: 100
              sell: 30
            arrow:
              slot: 14
              material: ARROW
              buy: 150
              sell: 30
            string:
              slot: 15
              material: STRING
              buy: 80
              sell: 8
            spider_eye:
              slot: 16
              material: SPIDER_EYE
              buy: 200
              sell: 10
            gunpowder:
              slot: 17
              material: GUNPOWDER
              buy: 300
              sell: 65
            blazerod:
              slot: 18
              material: BLAZE_ROD
              buy: 230
              sell: 50
            magma_cream:
              slot: 19
              material: MAGMA_CREAM
              buy: 160
              sell: 30
            slimeball:
              slot: 20
              material: SLIME_BALL
              buy: 160
              sell: 30
            ghast_tear:
              slot: 21
              material: GHAST_TEAR
              buy: 320
              sell: 80
            raw_cod:
              slot: 22
              material: COD
              buy: 30
              sell: 8
            cooked_cod:
              slot: 23
              material: COOKED_COD
              buy: 60
              sell: 15
            raw_salmon:
              slot: 24
              material: SALMON
              buy: 30
              sell: 8
            cooked_salmon:
              slot: 25
              material: COOKED_SALMON
              buy: 60
              sell: 15
            pufferfish:
              slot: 26
              material: PUFFERFISH
              sell: 150
            tropical_fish:
              slot: 29
              material: TROPICAL_FISH
              buy: 120
              sell: 30
            enderpearl:
              slot: 30
              material: ENDER_PEARL
              buy: 600
              sell: 180
            rabbit_foot:
              slot: 31
              material: RABBIT_FOOT
              buy: 70
              sell: 35
            rabbit_hide:
              slot: 32
              material: RABBIT_HIDE
              buy: 10
              sell: 5
            ink_sac:
              slot: 33
              material: INK_SAC
              buy: 50
              sell: 15
    materials:
      size: 54
      items:
        copper_ingot:
          slot: 10
          material: COPPER_INGOT
          buy: 200
          sell: 50
        coal:
          slot: 11
          material: COAL
          buy: 300
          sell: 80
        iron_ingot:
          slot: 12
          material: IRON_INGOT
          buy: 600
          sell: 200
        redstone_dust:
          slot: 13
          material: REDSTONE
          buy: 600
          sell: 150
        lapis_lazuli:
          slot: 14
          material: LAPIS_LAZULI
          buy: 600
          sell: 150
        gold_ingot:
          slot: 15
          material: GOLD_INGOT
          buy: 1000
          sell: 400
        diamond:
          slot: 16
          material: DIAMOND
          buy: 3000
          sell: 1200
        block_of_copper:
          slot: 19
          material: COPPER_BLOCK
          buy: 2000
          sell: 500
        block_of_coal:
          slot: 20
          material: COAL_BLOCK
          buy: 3000
          sell: 700
        block_of_iron:
          slot: 21
          material: IRON_BLOCK
          buy: 6000
          sell: 2000
        block_of_redstone:
          slot: 22
          material: REDSTONE_BLOCK
          buy: 5000
          sell: 1200
        block_of_lapis_lazuli:
          slot: 23
          material: LAPIS_BLOCK
          buy: 5500
          sell: 1500
        block_of_gold:
          slot: 24
          material: GOLD_BLOCK
          buy: 8000
          sell: 2000
        block_of_diamond:
          slot: 25
          material: DIAMOND_BLOCK
          buy: 30000
          sell: 8000
        emerald:
          slot: 30
          material: EMERALD
          buy: 3000
          sell: 1000
        netherite_ingot:
          slot: 32
          material: NETHERITE_INGOT
          buy: 5500
          sell: 1500
        block_of_emerald:
          slot: 39
          material: EMERALD_BLOCK
          buy: 25000
          sell: 2000
        block_of_netherite:
          slot: 41
          material: NETHERITE_BLOCK
          buy: 60000
          sell: 25000
    generals:
      size: 54
      items:
        glowstone_dust:
          slot: 9
          material: GLOWSTONE_DUST
          buy: 80
          sell: 20
        saddle:
          slot: 10
          material: SADDLE
          buy: 500
          sell: 100
        flint:
          slot: 11
          material: FLINT
          buy: 500
          sell: 30
        golden_apple:
          slot: 12
          material: GOLDEN_APPLE
          buy: 5000
        cobblestone:
          slot: 13
          material: COBBLESTONE
          buy: 10
          sell: 3
        netherite_upgrade:
          slot: 14
          material: NETHERITE_UPGRADE_SMITHING_TEMPLATE
          buy: 5000
        oak_log:
          slot: 15
          material: OAK_LOG
          buy: 50
          sell: 5
        spruce_log:
          slot: 16
          material: SPRUCE_LOG
          buy: 50
          sell: 5
        birch_log:
          slot: 17
          material: BIRCH_LOG
          buy: 50
          sell: 5
        jungle_log:
          slot: 20
          material: JUNGLE_LOG
          buy: 50
          sell: 5
        acacia_log:
          slot: 21
          material: ACACIA_LOG
          buy: 50
          sell: 5
        dark_oak_log:
          slot: 22
          material: DARK_OAK_LOG
          buy: 50
          sell: 5
        mangrove_log:
          slot: 23
          material: MANGROVE_LOG
          buy: 50
          sell: 5
        cherry_log:
          slot: 24
          material: CHERRY_LOG
          buy: 50
          sell: 5
    tools:
      size: 36
      items:
        diamond_pickaxe:
          slot: 11
          material: DIAMOND_PICKAXE
          name: '&7Diamond Pickaxe'
          buy: 7500
          sell: 2000
        diamond_axe:
          slot: 12
          material: DIAMOND_AXE
          name: '&7Diamond Axe'
          buy: 6000
          sell: 1500
        diamond_shovel:
          slot: 13
          material: DIAMOND_SHOVEL
          name: '&7Diamond Shovel'
          buy: 4000
          sell: 1000
        diamond_hoe:
          slot: 14
          material: DIAMOND_HOE
          name: '&7Diamond Hoe'
          buy: 4000
          sell: 1000
        fishing_rod:
          slot: 15
          material: FISHING_ROD
          name: '&7Fishing Rod'
          buy: 2000
          sell: 500
rtp:
  enabled: true
  cooldown: 2
  max_radius: 15000
  min_radius: 500
  max_attempts: 100
  delay: 2
  menu:
    title: "&0&l⟐ &b&lRANDOM TELEPORT &0&l⟐"
    rows: 3
    items:
      overworld:
        world: "world"
        slot: 12
        type: "GRASS_BLOCK"
        name: "&a&l⟐ OVERWORLD ⟐"
        lore:
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
          - ""
          - "&f&l✩ &7Teletrasportati nel mondo &a%world%"
          - "&f&l✩ &7Ambiente sicuro e familiare"
          - "&f&l✩ &7Perfetto per iniziare l'avventura"
          - ""
          - "&7▸ Stato del mondo: %status%"
          - "&7▸ Grandezza mondo: &e1000 x 1000"
          - "&7▸ Raggio: &e500-1500 blocchi"
          - "&7▸ Altezza: &e64-80 blocchi"
          - ""
          - "&a&l⟐ &aClicca per teletrasportarti! &a&l⟐"
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
      nether:
        world: "world_nether"
        slot: 13
        type: "NETHERRACK"
        name: "&c&l⟐ NETHER ⟐"
        lore:
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
          - ""
          - "&f&l✦ &7Teletrasportati nel mondo &c%world%"
          - "&c&l⚠ &cAttenzione: mondo estremamente pericoloso!"
          - "&f&l✦ &7Ricco di risorse rare e preziose"
          - "&f&l✦ &7Solo per giocatori esperti"
          - ""
          - "&7▸ Stato mondo: %status%"
          - "&7▸ Grandezza mondo: &e10000 x 10000"
          - "&7▸ Raggio: &e500-8000 blocchi"
          - "&7▸ Altezza: &e32-120 blocchi"
          - ""
          - "&c&l⟐ &cClicca per teletrasportarti! &c&l⟐"
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
      end:
        world: "world_the_end"
        slot: 14
        type: "END_STONE"
        name: "&5&l⟐ THE END ⟐"
        lore:
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
          - ""
          - "&f&l✦ &7Teletrasportati nel mondo &5%world%"
          - "&c&l⚠ &cAttenzione: mondo pericolosissimo!"
          - "&f&l✦ &7Dimora del Drago dell'End"
          - "&f&l✦ &7Risorse end-game uniche"
          - ""
          - "&7▸ Stato mondo: %status%"
          - "&7▸ Grandezza mondo: &e10000 x 10000"
          - "&7▸ Raggio: &e500-5000 blocchi"
          - "&7▸ Altezza: &e50-80 blocchi"
          - ""
          - "&5&l⟐ &5Clicca per teletrasportarti! &5&l⟐"
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
      info:
        world: ""
        slot: 26
        type: "COMPASS"
        name: "&e&l⟐ INFORMAZIONI RTP ⟐"
        lore:
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
          - ""
          - "&f&l✦ &7Random Teleport System"
          - "&f&l✦ &7Teletrasportati in posizioni casuali"
          - "&f&l✦ &7Esplora nuovi territori"
          - ""
          - "&e&l⚡ CARATTERISTICHE:"
          - "&7▸ Cooldown: &e5 secondi"
          - "&7▸ Tentativi max: &e20"
          - "&7▸ Delay teleport: &e5 secondi"
          - ""
          - "&a&l⟐ &aScegli un mondo per iniziare! &a&l⟐"
          - "&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
    fill_item:
      material: "BLACK_STAINED_GLASS_PANE"
      name: " "
  economy:
    enabled: false
    cost: 0.0
  sound:
    enabled: true
    sound: "ENTITY_ENDERMAN_TELEPORT"
    volume: 1.0
    pitch: 1.2
  worlds:
    overworld:
      enabled: true
      max_radius: 15000
      min_radius: 1000
      max_y: 256
      min_y: 64
      safe_blocks:
        - "GRASS_BLOCK"
        - "DIRT"
        - "STONE"
        - "SAND"
        - "GRAVEL"
      unsafe_blocks:
        - "LAVA"
        - "WATER"
        - "FIRE"
        - "MAGMA_BLOCK"
    nether:
      enabled: true
      max_radius: 8000
      min_radius: 500
      max_y: 120
      min_y: 32
      safe_blocks:
        - "NETHERRACK"
        - "NETHER_BRICKS"
        - "BLACKSTONE"
      unsafe_blocks:
        - "LAVA"
        - "FIRE"
        - "MAGMA_BLOCK"
        - "SOUL_FIRE"
    end:
      enabled: true
      max_radius: 5000
      min_radius: 500
      max_y: 80
      min_y: 50
      safe_blocks:
        - "END_STONE"
        - "PURPUR_BLOCK"
      unsafe_blocks:
        - "VOID_AIR"
        - "END_PORTAL"

economy:
  heart_price: 75000
  unban_price: 300000

gui:
  buy_menu:
    title: "&8Lifesteal Menu"
    rows: 3
    heart_item:
      material: "RED_DYE"
      name: "&cCuore"
      slot: 12
      lore:
        - "&8&m--------------------"
        - "&f• &7Acquista un cuore extra!"
        - "&f• &7Costo: &e%economy_heart_price% $"
        - "&f• &7Soldi: &e$%economy_balance%"
        - ""
        - "%economy_enough_heart%"
        - "&8&m--------------------"
    unban_item:
      material: "END_CRYSTAL"
      name: "&bUnBan"
      slot: 14
      lore:
        - "&8&m--------------------"
        - "&f• &7Acquista il tuo unban istantaneamente!"
        - "&f• &7Costo: &e%economy_unban_price% $"
        - "&f• &7Soldi: &e$%economy_balance%"
        - ""
        - "%economy_enough_unban%"
        - "&8&m--------------------"

commands:
  rtp:
    permission: "lifecore.rtp.use"
  sellgui:
    permission: "lifecore.sellgui"
  lifecore_spawn_bypass:
    permission: "lifecore.spawn.bypass"
  lifecoreadmin:
    permission: "lifecoreadmin.use"
    aliases:
      - lca
      - lifeadmin
  lifecoreadmin_help:
    permission: "lifecoreadmin.help"
  lifecoreadmin_heart_add:
    permission: "lifecoreadmin.heart.add"
  lifecoreadmin_heart_remove:
    permission: "lifecoreadmin.heart.remove"
  lifecoreadmin_heart_list:
    permission: "lifecoreadmin.heart.list"
  lifecoreadmin_ban:
    permission: "lifecoreadmin.ban"
  lifecoreadmin_unban:
    permission: "lifecoreadmin.unban"
  lifecoreadmin_setspawn:
    permission: "lifecoreadmin.setspawn"
  lifecoreadmin_reload:
    permission: "lifecoreadmin.reload"
```

</details>

---

## Planned Features 🔜

- **Custom Economy System** with LivingCoins.  
- **Custom Gamemodes** for unique gameplay experiences.  
- **Advanced Teleport System** (`/tp`, `/tpa`, etc.).  
- **Dynamic Leaderboards** with new placeholders and auto-setup holograms.  
- **Custom Fly, Vanish & StaffMode** for staff management.  
- **Daily/Weekly/Monthly Missions & Rewards**.  
- **Custom CoinFlip** for in-game betting.   

---

## Support 🛡️

- **Bug Reports**: Open an issue on [GitHub](https://github.com/BinaryCodee/LifeCore/issues) with details (server version, plugin version, error logs).  
- **Discord**: [Join our Discord](https://discord.gg/BsYQhyv99j)

---

## Notes 📝

- **Minecraft Versions**: 1.21.x.  
- **Tested With**: Spigot and Paper  
- **Dependencies**: Vault (required), PlaceholderAPI (optional).  
- **Team Reset**: Due to the new team system, existing teams must be recreated. Free crate keys will be distributed as compensation.  
- **Testing Server**: play.livingpvp.eu

