---
layout: default
title: jLinguist - Simple Java properties file editor
---
# About

jLinguist is an Java property file editor designed to help translating Java applications into different languages.
It provides simple and easy to use interface so you wont have to deal with properties files syntax. It also automatically
escapes all of the special characters helping ensure that files are always readable by Java.

![jLinguist main window]({{ site.github.url }}/assets/main.png)

# Download

- [jLinguist v1.0 - 4MB](https://github.com/ankalag0n/jLinguist/releases/latest) -
This is Java jar executable file. It can be run on any operating system that have Java virtual machine (version 8 or later) installed.
If you don't have Java Runtime Environment installed in your system please visit [http://java.com](http://java.com) and download the latest version.
You can run this app by double clicking on its icon or executing `java -jar jLinguist-1.0.jar` command inside the console/terminal.

# Getting started

Start by opening ![Open]({{ site.github.url }}/assets/open.png) property files you want to edit. You don't have to open each of the
languages file separately. Just pick one of the file and others will be loaded automatically. For example you have files:
`MessageBundle.properties`, `MessageBundle_de.properties` and `MessageBundle_fr.properties` just pick one of theme and
all others will be loaded as well. Once files are loaded you will see all of the languages corresponding to the names of the
files in the panel on the left. Just click the checkbox near the languages you want to edit and phrases from those languages
will be displayed in the columns in the center. To edit the phrase double-click on it. To commit changes to the phrase press
enter. To edit long phrases in more comfortable way right click on the selected phrase and choose ![Multiline editor]({{ site.github.url }}/assets/multiline.png) *Multiline editor*.

![jLinguist multiline editor]({{ site.github.url }}/assets/editor.png)

## Keyboard shortcuts

You can work much more efficient without removing your hands from the keyboard. To do so you can use few keyboard shortcuts.
First after selecting one of the cells in the table you can move to a different cell by clicking **arrow keys** on your keyboard.
To edit the phrase just press **enter** and the edit field will show up. Press **enter** again to close the editor and commit changes.
Press **escape** to cancel editing and discard changes. To open multiline editors window just press **alt + enter**.
Multiline editors window will popup and if the phrase contains some text it will be selected so you just have to start typing to
change it. After the editing is complete press **alt + enter** again to close multiline window and commit your changes.
You can also use standard copy and paste shortcuts (**ctrl + c**, **ctrl +v** on Windows and **cmd + c**, **cmd + v** on Mac)
to copy and paste text directly into the cells without opening the editor.