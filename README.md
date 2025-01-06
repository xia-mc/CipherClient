# CipherClient
### Injectable utility mod for Minecraft 1.7.10

---

[Readme disponible en français](README-fr.md)

## Disclaimer ⚠️

This mod should not be used on a non-anarchy server. It is not a "ghost client" and is not designed to be undetectable. Any use in a non-anarchy will result in a ban !

## Origin

Some time ago, [FuzeIII announced the creation of "Palanarchy"](https://www.youtube.com/watch?v=Gqm2eMAcUg8&t=310s), an anarchy Minecraft server with Paladium mods. I assumed there would be a default utility mod, but that it would be limited and there would be no official way to modify the game ourselves. That's why I decided to create this mod, which can be injected into any Forge 1.7.10 instance. However, it turned out that Palanarchy was actually on 1.16.5 (and only had a limited version of the Palamod, as well as being excessively P2W). So my mod was useless! I still decided to publish it as a PoC, but I do not encourage its use (as to my knowledge, there is no anarchy server in 1.7.10).

## Usage
1. Download the injector from the [latest release](/../../releases/tag/latest) page
2. Double click it or run `java -jar cipher-client-1.0-injector.jar inject <pid>`

## Build
```shell
./gradlew setupDecompWorkspace
./gradlew build
```

The build should be located in `build/libs`

## Credits
- [radioegor146](https://github.com/radioegor146) for [ehacks-pro](https://github.com/radioegor146/ehacks-pro) and [jar-to-dll](https://github.com/radioegor146/jar-to-dll)
- [MeteorDevelopment](https://github.com/MeteorDevelopment) for [meteor-client](https://github.com/MeteorDevelopment/meteor-client)
- [VazkiiMods](https://github.com/VazkiiMods) for [Neat](https://github.com/VazkiiMods/Neat/tree/c5961631ddcdb02a95f262e910ddd7b46c168278)
- [superblaubeere27](https://github.com/superblaubeere27) for [ClientBase](https://github.com/superblaubeere27/ClientBase)
- [MinecraftForge](https://github.com/MinecraftForge) for the mappings

## Licensing
This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

If you use **ANY** code from the source:
- You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source and/or obfuscated application.
- You must state clearly and obviously to all end users that you are using code from this project.
- Your application must also be licensed under the same license.
