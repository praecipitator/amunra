What is this
============
This is an addon for the Minecraft Mod GalactiCraft.
See this forum thread for more information and screenshots: https://forum.micdoodle8.com/index.php?threads/.5985/

COMPILING and stuff
===================
- download these 4 files from https://micdoodle8.com/mods/galacticraft/downloads/dev:
-- Galacticraft-API-1.7-3.0.12.463.jar
-- GalacticraftCore-Dev-1.7-3.0.12.463.jar
-- Galacticraft-Planets-Dev-1.7-3.0.12.463.jar
-- MicdoodleCore-Dev-1.7-3.0.12.463.jar
- put them under /libs/
- cd into whereever you checked out the code
- do this:
    gradlew.bat setupDecompWorkspace
    or
    ./gradlew setupDecompWorkspace
- at this point, you should be able to build the mod using gradlew.bat build. The result should end up under /build/libs
- Now, for eclipse, try running gradlew.bat eclipse
- If you are lucky, this should work: open eclipse, select the "eclipse" directory within the code directory as workspace, and you get a project called "Minecraft" all set up
- If not (and let's face it, this is way more likely), well, tough luck, I have no idea. Sorry.
-- Try running this: gradlew cleancache --refresh-dependencies setupDecompWorkspace eclipse
-- Have a look here: http://www.minecraftforge.net/forum/index.php?topic=14048.0 basically you will need to download the source code of forge, and paste it into my code directory, and then run the commands as described there.
-- Seriously, I have no idea. For me it worked somehow. Once. I couldn't reproduce it afterwards. If you can make it work all the time, you are welcome to send me a pull request.
