# Platypus Client
A terribly made, work in progress, Fabric client-side mod. Made from Fabric's mod template generator https://fabricmc.net/develop/template/.
<br>
<br>
This is purely made for fun. There are much better, open source cheat clients such as [Wurst Client](https://github.com/Wurst-Imperium/Wurst7) and [Meteor Client](https://github.com/MeteorDevelopment/meteor-client)
## Build
Make sure you are using a JDK for Java 21 (I used Adoptium Temurin)
1. Clone the project
```git
git clone https://github.com/ReactorFailure/PlatypusClient.git
```
2. Build the project
```
./gradlew build
```
Once the build is successful, the jar will be located in `build > libs`

## Installation
Go to the repo's [releases page](https://github.com/ReactorFailure/PlatypusClient/releases) and make sure to download the jar file for the **right Minecraft version**

## Supported Minecraft Versions
- 1.21.11

## Dependencies
> [!TIP]
> Use [Prism Launcher](https://prismlauncher.org/) to easily download mods

| Mod                                               | Required? | Notes                                                                                                                                                                                                                             |
|---------------------------------------------------|-----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Fabric API](https://modrinth.com/mod/fabric-api) | ✅         | -                                                                                                                                                                                                                                 |
| [ModMenu](https://modrinth.com/mod/modmenu)       | ❌         | **Not required but recomended** if you want to access the mod's settings in the mod menu . You will also need to download the [Text Placeholder API](https://modrinth.com/mod/placeholder-api) as it is a dependency for ModMenu. |

## Usage
> [!NOTE]
> Keybinds can be changed in Minecraft's settings

- Press `G` to open/close the mod's dashboard

## Features
- Discord rich presence
- Cheat modules
- Other QOL changes maybe


## Future plans
See the repo's issue page [here](https://github.com/ReactorFailure/PlatypusClient/issues).
Feel free to make any PRs / tell me if some of my ideas are unrealistic.
