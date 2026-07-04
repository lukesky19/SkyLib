# SkyLib
## Description
* A library containing shared code across many of my projects.

## Dependencies
* PlaceholderAPI (Paper Only)

## FAQ
Q: What versions does this plugin support?

A: 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11, 26.1, 26.1.1, 26.1.2, and 26.2.
Note: Java 25 is required even on versions older than 26.1.

Q: I get the following error: "SkyLib has been compiled by a more recent version of the Java Runtime 
(class file version 69.0), this version of the Java Runtime only recognizes class file versions up to 65.0"

A: SkyLib is compiled using Java 25 as part of it's support of 26.1 and beyond. SkyLib still works on older version as long as Java 25 is used.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot? Paper? Velocity? (Insert other server software here)?

A: SkyLib works on Paper servers and Velocity servers. This will likely also work on forks of Paper and Velocity (untested). There are no plans to support any other server software (i.e., Spigot or Folia).

## For Server Admins/Owners
* Download the plugin from the releases tab and add it to your server.

## For Developers
```./gradlew build```

```koitlin
compileOnly("com.github.lukesky19:SkyLib:2.0.0.0")
```

## Why MIT?
I wanted a license that will keep my code open source while allowing as many people as possible to use this library if they so chose.
