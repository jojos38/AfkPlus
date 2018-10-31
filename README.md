# AfkPlus
Afk plugin for Spigot

# What it does :
 - Differents settings for when the player is no longer AFK (Send message, Move camera, Move, Interact with blocks, Interact with entity, Interact with air)
 - Edit all messages (Afk, No longer afk, Command permission, Player not found)
 - Edit player name in TAB list when they are afk (support colors) (exemple : [AFK] playername)
 - Set the time before a player is afk
 - Set afk other players if you have permission
 - Toggle afk with command

# Permissions list :
 - afkplus.afk - Set yourself afk or not
 - afkplus.afkplayers - Set other players afk
 - afkplus.beingafk - Permission to be afk

# Commands list :
 - /afk - Set yourself afk or not
 - /afk [player] - Set other player afk

# config.yml :  
\# How many seconds before a player get Afk  
time: 300  

\# What player can do to remove Afk status  
\# Interact mean if the player left click or right click on any block  
\# Interact with entity mean if the player hit or get hit by a player or a mob  
send-message: true  
move-camera: false  
move: true  
interact-with-blocks: true  
interact-with-entity: true  
interact-with-air: false  

\# Afk command  
player-not-found-message: 'Player {player} not found.'  

\# Afk in tab list  
show-afk-in-tab-list: true  
afk-tab-list-display: '{player} &7[afk]'  

\# No longer afk in tab list  
no-longer-afk-tab-list-display: '{player}'  

\# Afk  
enable-afk-messages: true  
afk-message: '&8{player} &7is afk.'  

\# No longer Afk  
enable-no-longer-afk-messages: true  
no-longer-afk-message: '&8{player} &7is no longer afk.'  

\# Permission message  
permission-message: "&cYou don't have permission to do this."  
