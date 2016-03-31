#BetterMinecraftChat

_BetterMinecraftChat_ is a client-side mod that provides various tweaks and features to the Minecraft chat, while staying close to the look and feel of the Vanilla chat.

It's most notable features are Emoticons, and Image Link preview, configurable Chat Tabs and Message Filters. It also provides an API for other mods to use and will act as the new base for my upcoming IRC & Twitch Integration mods.

##Useful Links
* [Latest Builds](http://jenkins.blay09.net/job/BetterMinecraftChat%201.9/) on my Jenkins
* [CurseForge](http://minecraft.curseforge.com/projects/betterminecraftchat) Page with Downloads
* [@BlayTheNinth](https://twitter.com/BlayTheNinth) on Twitter

##API
I recommend not using it at this point. It is likely to get adjustments as I start working on my new IRC and Twitch integration mods.
If you don't mind possible breaking changes in upcoming versions, go ahead; I'll be documenting the API once it's finalized.

The easiest way to add BetterMinecraftChat to your development environment is to do some additions to your build.gradle file. First, register the maven repository by adding the following lines:

```
repositories {
    maven {
        name = "eiranet"
        url ="http://repo.blay09.net"
    }
}
```

Then, add a dependency to either just the API (api) or, if you want BMC to be available while testing as well, the deobfuscated version (dev):

```
dependencies {
    compile 'net.blay09.mods:BetterMinecraftChat:major.minor.build:dev' // or just api instead of dev
}
```

Make sure you enter the correct version number for the Minecraft version you're developing for. The major version is the important part here; it is increased for every Minecraft update. See the jenkins to find out the latest version number.
Done! Run gradle to update your project and you'll be good to go.
The latest API and an unobfuscated version of the mod can also be downloaded from my [Jenkins](http://jenkins.blay09.net), if you're not into all that Maven stuff.