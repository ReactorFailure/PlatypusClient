![Platypus Client Banner](src/main/resources/assets/platypusclient/textures/gui/title/banner.png)
<hr>

# Platypus Client
A terribly made, work in progress, Fabric client-side mod. Made from Fabric's mod template generator https://fabricmc.net/develop/template/.
<br>
<br>
This is purely made for fun. There are much better, open source cheat clients such as [Wurst Client](https://github.com/Wurst-Imperium/Wurst7) and [Meteor Client](https://github.com/MeteorDevelopment/meteor-client)
<br>
<br>
Feel free to contribute and make PRs/Issues.

## Documentation (not made yet)
The documentation can be found in the docs directory of the project or in the project's Github wiki [here](https://github.com/ReactorFailure/PlatypusClient/wiki)

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
Once the build is successful, the jar will be located in `build > libs` (don't use the jar that has "source" in it).

## Installation
Go to the repo's [releases page](https://github.com/ReactorFailure/PlatypusClient/releases) and make sure to download the jar file for the **right Minecraft version**. Once the mod finished downloading, drag and drop the jar to the mod folder.

## Supported Minecraft Versions
| MC Version | Supported Mod Version(s) | Notes                                                                                                                                                                                                                       |
|------------|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 26.1 =<    | ❌                        | I will update the mod to this version after I finish all the features I want in 1.21.11 (if that ever happens).                                                                                                             |
| 1.21.11    | 🟡                       | Currently, the mod only supports this mc version but as of now, you can only get the mod by cloning and building the project yourself. Once all the features I want has been implemented, I will list `v1.0.0` on this row. |

## Dependencies
> [!TIP] 
> Use [Prism Launcher](https://prismlauncher.org/) to easily download mods

| Mod                                               | Required? | Notes                                                                                                                                                                                                                            |
|---------------------------------------------------|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Fabric API](https://modrinth.com/mod/fabric-api) | ✅         | -                                                                                                                                                                                                                                |
| [ModMenu](https://modrinth.com/mod/modmenu)       | ✅/❌       | **Not required but nice to have** if you want to access the mod's settings in a mod menu. You will also need to download the [Text Placeholder API](https://modrinth.com/mod/placeholder-api) as it is a dependency for ModMenu. |

## Usage
> [!NOTE]
> Keybinds can be changed in Minecraft's settings

- Press `G` to open/close the mod's dashboard

## Features
- Discord rich presence
- Cheat modules
- QOL changes (Text wrapping tooltip, scrollable tooltip, and shulker tooltip)
