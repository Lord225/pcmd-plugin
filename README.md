PCMD Plugin 🐾
Welcome to the Personal Commandblock Plugin
This plugin allows you to create safe command blocks for users with Tellraw commands in it with some feline twist. 
Whether you’re building interactive blocks or just want to add some extra paw-sitive energy to your server, this plugin will bring a relief in debugging your redstone devices, meow!

# Commands 🐱
```
/pcmd all/me <text/legacy/tellarawjson>
```
Usage: Create and manage your personal command blocks with various text formats, including Tellraw JSON or legacy text format
* me - gives you PCMD block with selector set to nick of player
* all - gives you PCMD block with selector set to `@a[distance=..100]`

In JSON format you can specify any 
```
/pcmd me &7 Hello World
/pcmd all Hello, world!
/pcmd all ["",{"text":"Welcome to "},{"text":"PCMD","color":"red"}]
```

# Permissions 🐾
`rf2.pcmd.me`: Grants access to the /pcmd me command.
`rf2.pcmd.all`: Grants access to the /pcmd all command.
Make sure your players have the right permissions to interact with the PCMD blocks
