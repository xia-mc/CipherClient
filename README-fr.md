# CipherClient
### Mod utilitaire injectable pour Minecraft 1.7.10

---

## Disclaimer ⚠️

Ce mod ne doit pas être utilisé sur un serveur non anarchique. Ce n'est pas un "ghost-client" et il n'est pas conçu pour être indétectable. Toute utilisation vous conduira à un bannissement !

## Origine

Il y a quelque temps, [FuzeIII a annoncé la création de "Palanarchy"](https://www.youtube.com/watch?v=Gqm2eMAcUg8&t=310s), un serveur Minecraft anarchique avec les mods de Paladium. J'ai supposé qu'il y aurait un mod utilitaire par défaut, mais qu'il serait limité et qu'il n'y aurait pas de moyen officiel de modifier le jeu nous-mêmes. C'est pourquoi j'ai décidé de créer ce mod, qui peut s'injecter dans n'importe quelle instance de Forge 1.7.10. Cependant, il s'est avéré que Palanarchy était en réalité en 1.16.5 (et qu'il n'avait qu'une version limitée du palamod, en plus d'être excessivement P2W). Mon mod était donc inutile ! J'ai quand même décidé de le publier comme une PoC, mais je n'encourage en aucun cas son utilisation (car à ma connaissance, il n'y a pas de serveur anarchique en 1.7.10)

## Usage
Double cliquez dessus ou exécutez `java -jar cipher-client-1.0-injector.jar inject <pid>`

## Build
```shell
./gradlew setupDecompWorkspace
./gradlew build
```
La build doit se trouver dans `build/libs`

## Credits
- [radioegor146](https://github.com/radioegor146) pour [ehacks-pro](https://github.com/radioegor146/ehacks-pro) et [jar-to-dll](https://github.com/radioegor146/jar-to-dll)
- [MeteorDevelopment](https://github.com/MeteorDevelopment) pour [meteor-client](https://github.com/MeteorDevelopment/meteor-client)
- [VazkiiMods](https://github.com/VazkiiMods) pour [Neat](https://github.com/VazkiiMods/Neat/tree/c5961631ddcdb02a95f262e910ddd7b46c168278)
- [MinecraftForge](https://github.com/MinecraftForge) pour les mappings

## License
Ce projet est sous licence [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.fr.html).

Si vous utilisez **N'IMPORTE QUEL** code provenant de la source :
- Vous devez divulguer le code source de votre travail modifié ainsi que le code source que vous avez pris de ce projet. Cela signifie que vous n'êtes pas autorisé à utiliser du code de ce projet (même partiellement) dans une application à code source fermé et/ou obfusqué.
- Vous devez indiquer clairement et de manière évidente à tous les utilisateurs finaux que vous utilisez du code provenant de ce projet.
- Votre application doit également être sous licence de la même licence.
