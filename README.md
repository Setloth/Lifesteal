# Lifesteal

Lifesteal is a [Minehut Product](https://www.echology.page/socials/lifesteal) that brings Lifesteal to your server!

> This product can only be used on a Minehut server

## Guide

This will detail each part of the [config.yml](https://github.com/Echological/Lifesteal-MH/blob/main/src/main/resources/config.yml) used for configuration of the plugin

### Health

The very first section of the config details the configuration for configuring how much health to give players when they get a kill or die, and to configure bounds for the Health

#### multiplier

Multiplier section configures the amount of lives that players will gain or lose respectively 

##### `gain` & `loss`

These sections have 2 parts, `enabled` and `value`:

`enabled` - a `true`/`false` value that determines if the multiplier should be applied or not

`value` - a number value that determines what the gain multiplier should be

This value will be the number of HP that the player will gain or lose when they either get a kill or get killed respectively

*Note: HP is not the same as hearts, 2 HP = 1 heart, when you have 10 hearts, you have 20 HP*

#### `upper-limit` & `lower-limit`

These two sections dictate the upper and lower bounds respectively for the maximum/minimum HP of the player

These sections both have 2 parts, `enabled` and `value`:

`enabled` - a `true`/`false` value that determines if the bound should be checked

`value` - a number value that determines what the bound should be

For `upper-limit`, if the player's HP is already at the value when gaining hearts, and `enabled` is `true`, then no hearts will be applied

For `lower-limit`, if the player's HP is at the value when losing hearts, then they will be out of hearts, which you can configure actions for later

### Kills

This section of the configuration details the configuration for kill-based items of the config, like the mesages to send

#### `pvp` & `other`

These two sections designate kill sections for players and other deaths respectively,

`pvp` -> When a player kills a player

`other` -> when a player dies to a non-player (a mob, fall damage, etc.)

Both of the sections have `enabled`, `victim`, and `death-message` properties

`enabled` - a`true`/`false` value that represents if kills in that section should be listened for

*Note: the `other` section is disabled by default, if you wish to take lives for deaths by non-player deaths, set the `enabled` property within this section to `true`*

The `pvp` section also has an `attacker` section

#### `attacker`

*This section is only in the `pvp` section*

The `attacker` section designates the message configuration for the message that is sent to the attacker player

In this section, there is a `chat-message` section, which has two parts: `enabled` and `value`

`enabled` - a `true`/`false` value that represents if the message to the attacker should be sent or not

`value` - a text value that represents the message to be sent

Placeholders:
  `%victim%` - the name of the victim
  `%attacker%` - the name of the attacker
  `%victim-lives%` - the lives of the victim
  `%attacker-lives%` - the lives of the attacker

##### `victim`

The `victim` section within both `pvp` and `other` designates the message configuration for the message sent to the victim player

In this section, there is a `chat-message` section, which has two parts: `enabled` and `value`

`enabled` - a `true`/`false` value that represents if the message to the victim should be sent or not

`value` - a text value that represents the message to be sent

Placeholders:
  `%victim%` - the name of the victim
  `%attacker%` - the name of the attacker
  `%victim-lives%` - the lives of the victim
  `%attacker-lives%` - the lives of the attacker

##### `death-message`

The `death-message` section within both `pvp` and `other` configures the death message sent by Minecraft when you die, this has two parts aswell, `enabled` and `value`

`enabled` - a `true`/`false` value that represents if the death message should be altered or not

`value` - a text value of what the death message should be

Placeholders:
  `%victim%` - the name of the victim
  `%attacker%` - the name of the attacker
  `%victim-lives%` - the lives of the victim
  `%attacker-lives%` - the lives of the attacker
  
### Out-Of-Lives

This section of the configuration details the configuration for when a player runs out of lives

Being out of lives is when you fall below the `lower-limit` from the `Health` section

#### message

The `message` section configures the message that will be sent to the player when they run out of lives

It has two parts, `enabled` and `value`:

`enabled` - a `true`/`false` value that represents if the message should be sent

`value` - a text value of what the message to send should be

#### command

The `command` section configures the commands that will be sent to the console when a player runs out of lives

This section has two parts, `enabled` and `value`:

`enabled` - a `true`/`false` value that represents if the commands should be run or not

`value` - a **list** of text values that represents the commands to be run

An example of a list is:
```yml
value:
  - "some value"
  - "some value2"
  - "some value3"
```
**OR**
```yml
value: ["some value", "some value2", "some value3"]
```

Either of those formats work, just make sure to change the values of the texts to what your want your commands to be

Placeholders:
  `%player%` - the name of the player
  
#### `force-spectator`

`force-spectator` is a `true`/`false` value in the `Out-Of-Lives` that controls if you wish to automatically set the player to the `spectator` gamemode when they run out of lives

### Crafting

This section configures everything to do with crafting, it is quite complicated so make sure to read carefully!

This section has 6 different properties/sections inside of it:

`enabled`, `lives`, `item`, `shape`, `ingredients` and `auto-version`

`enabled` - a `true`/`false` value that determines if the entire crafting section should be used or not, setting this to `false` will not register your crafting or allow your items to works, setting this to `true` will allow crafting to be enabled after a reload (`/lifesteal reload` or a server restart)

`lives` - a number value that determines the amount of lives each crafted heart will give to a player using it

`auto-version` - a `true`/`false` value that will automatically save and update your file when reloaded to use the latest version of items for your server

*Warning: using `auto-version` will erase your comments in the file, if you need to you can reference the default file [here](https://github.com/Echological/Lifesteal-MH/blob/main/src/main/resources/config.yml)*

#### item

The `item` section determines the configuration for the item that will be crafted

This section uses the builtin parser for items, which means you are required to have the `==:` property and its value must be `org.bukkit.inventory.ItemStack`

To learn more about the ways to configure this section, please reference [spigot's docs](https://www.spigotmc.org/wiki/itemstack-serialization/)

#### shape

The `shape` value will configure the layout of the items configured in the `ingredients` section

`shape` is a **list** of text that will configure each of the 3 lines of the crafting table's items

Example layout:

```yml
shape:
  - "AAA" # this is the first line of the crafting table
  - "BBB" # this is the second line
  - "CCC" # this is the third line
```

You **must** have 3 lines in this value, or it will not be able to properly configure your crafting recipe

Each line of the `shape` value must have 3 characters, one for each of the slots in the crafting table

Each of the characters in the `shape` value will correspond to the property name of the item you want in the `ingredients` section, which I will talk about now

#### ingredients

The `ingredients` section is a section of sections, **not a list**, that will configure your items for the crafting recipe

Example layout:

```yml
ingredients:
  A: # this is the property name for item "A"
    ==: org.bukkit.inventory.ItemStack
    #... more item configuration
```

In this example, I made a section within `ingredients` called `A`

`A` is the property name for this section, and that is what you will use in the `shape` section to reference this item

Then, the `A` section is an item, like we configured for the `item` section earlier, to learn more about this configuration, visit [spigot's docs](https://www.spigotmc.org/wiki/itemstack-serialization/)

You can create as many sections as you wish in the `ingredients` section and you can call them different characters, but there are some limitations:
1. You can not have two of the same property name, for example, you can not have two sections with the property name of `B`
2. If you make an item here, it should be included in your shape, unused items will cause problems registering your recipe
3. Each property name has to be one character, no more:

    Valid property names: `A`, `J`, `C`, `0`, `5` and anything that is one character text

    Invalid property names: `Wo`, `Item`, `item`, ` `, `_____` and anything that is not a one character text

4. Make sure your section follows the correct structure for an item, once again, reference [spigot's docs](https://www.spigotmc.org/wiki/itemstack-serialization/) for more info

For property names, I recommend that you use letters of the english alphabet: A,B,C,D,E,F...

Once you have made your ingredients section, then make sure to update your `shape` section using those property names

---

And that is all for the configuration of the config file

If you continue to have problems configuring your plugin, make sure to read each part of this and look at the comments within the config file, if all else fails, join the discord server [here](https://www.echology.page/socials/discord)

