# Golf Solitaire

A desktop Java Swing implementation of Golf Solitaire with strategy-driven computer play,
statistics, and local card image resources.

## Requirements for Development

- JDK 17 or newer
- Windows for the `jpackage` app-image task

The Gradle wrapper is included, so Gradle does not need to be installed separately.

## Run From IntelliJ IDEA

Run the main class:

```text
org.example.Main
```

## Run With Gradle

```powershell
.\gradlew.bat run
```

## Build Runnable JAR

```powershell
.\gradlew.bat clean build
```

Output:

```text
build\libs\golf-solitaire.jar
```

Run:

```powershell
java -jar build\libs\golf-solitaire.jar
```

The card images are packaged from:

```text
src\main\resources\cards
```

They are loaded from the classpath, so the JAR does not depend on local absolute image paths.

## Build Windows App Image With jpackage

```powershell
.\gradlew.bat packageWindowsAppImage
```

Output executable:

```text
build\jpackage\GolfSolitaire\GolfSolitaire.exe
```

This app image includes a Java runtime. The user does not need to install Java separately.

Important: keep the whole generated folder together:

```text
build\jpackage\GolfSolitaire
```

The `.exe` depends on the runtime and app files in that folder. You can copy the whole
`GolfSolitaire` folder to another Windows machine and run `GolfSolitaire.exe`.

## True Single-File EXE Notes

A true single `.exe` that contains the game, Java runtime, Swing UI support, and all card
images is not what `jpackage` normally produces. `jpackage` creates a reliable app folder
or installer-style package.

Potential alternatives:

- Launch4j can wrap a JAR as an `.exe`, but it usually still needs a bundled or installed
  JRE next to it.
- GraalVM Native Image can sometimes produce a native `.exe`, but Swing/AWT applications
  and resource loading make it more complex and riskier than `jpackage`.

Recommended release format for this project right now:

```text
build\jpackage\GolfSolitaire
```

Distribute that folder as a ZIP file.

Optional future investigation with GraalVM:

```powershell
native-image -jar build\libs\golf-solitaire.jar GolfSolitaire
```

This requires GraalVM Native Image to be installed and may need extra configuration for
Swing/AWT and packaged resources.

## Statistics Storage

Statistics are stored in the user's home directory:

```text
.golf-solitaire-stats.properties
```

This means statistics work when running from IntelliJ, from the runnable JAR, or from the
`jpackage` app image.
