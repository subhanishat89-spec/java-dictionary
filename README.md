## ✨ Features
- 🔍 Case-insensitive lookup with "did you mean" suggestions
- ➕ Add, ✏️ update, and 🗑️ delete words
- 🔎 Search by prefix and list all words
- 💾 Persistent UTF-8 storage in a plain text file (`word:definition`)
- 🛡️ Input validation, overwrite confirmation, and safe saving (temp file, then replace)
- 🔤 Alphabetical ordering with `TreeMap`

## 🛠️ Tech Stack
Java · `java.nio.file` · Collections (`TreeMap`) · Separation of concerns (logic in `Dictionary`, UI in `Main`)

## 🚀 How to Run
Requires JDK 8 or newer.
```bash
git clone https://github.com/subhanishat89-spec/java-dictionary.git
cd java-dictionary
javac -d out src/dictionary_final/*.java
java -cp out dictionary_final.Main
```
Press Enter at the first prompt to use the default `dictionary.txt`.

## 📋 Sample Run
```
Dictionary file path [dictionary.txt]:
Loaded 5 word(s) from dictionary.txt

===== Dictionary =====
1. Look up a word
...
Choose an option: 1
Enter a word: Java
java: A popular object-oriented programming language
```
