[![](https://jitpack.io/v/nichbar/YourName.svg)](https://jitpack.io/#nichbar/YourName)
# YourName
YourName is a Android library.<br/> 
You can generate your own Chinese name with it.
## Download
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Then add the dependency:
```groovy
dependencies {
	compile 'com.github.nichbar:YourName:v1.0'
}
```
## Usage
### Generate a random Chinese name with three characters
Use ```generateName()```
```java
YourName yourName = new YourName();
Toast.makeText(this, yourName.generateName(), Toast.LENGTH_SHORT).show();
```
### Generate a random Chinese name with custom length
Use ```generateName(int length)```
```java
YourName yourName = new YourName();
Toast.makeText(this, yourName.generateName(YourName.TWO_CHARACTER), Toast.LENGTH_SHORT).show();
```
### Only family name
Use ```generateFamily(int length)```
```java
YourName yourName = new YourName();
Toast.makeText(this, yourName.generateFamily(YourName.ONE_CHARACTER), Toast.LENGTH_SHORT).show();
```
### Only given name
Use ```generateGivenName()```, only one character will return. You may call twice to get two characters.
```java
YourName yourName = new YourName();
Toast.makeText(this, yourName.generateGivenName(), Toast.LENGTH_SHORT).show();
```
## TODO
- [ ] Generate names with FengShui option.

## Thanks
>[chinese-random-name](https://github.com/XadillaX/chinese-random-name) by [XadillaX](https://github.com/XadillaX)
