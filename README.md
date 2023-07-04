
# FontTools - *Make png files out of Font Characters.*
It contains different tools such as (Font to Single Character in .png format) converter .etc
## Use Case Scenarios : 
 1. The Generated png files can be helpful in creating WatchFaces.

## Requirements

- Java 8 [Oracle JDK or any OpenJDK with OpenJFX]
- Maven

## Tech

- [opentype4j](https://github.com/Jkanon/opentype4j) - A library to Parse OpenType Fonts
- [zt-zip](https://github.com/zeroturnaround/zt-zip) - Library to Create zip files.


## Installation
Get the executable jar in target folder

    mvn clean package
    
## Run
    
    java -jar fonttools-<version>.jar