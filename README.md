# SC4FT (Syntax Checker for Fix Templates)


I. Requirements
--------------------
 - [Java 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)


II. Declaration
--------------------
 - The syntax checker used in TRANSFER(ICSE'22) for the constructions of the two large-scale datasets (Dataset_FL and Dataset_PR).

III. Usage
--------------------
```
java -jar SC4FT-1.0-SNAPSHOT-jar-with-dependencies.jar <data_dir> true/false
e.g. java -jar SC4FT-1.0-SNAPSHOT-jar-with-dependencies.jar /path_to_this_project/data/ true
```
true: allow multipy categories for a certain code sample, which is used to generate Dataset_FL.
flase: forbid multipy categories for a certain code sample, which is used to generate Dataset_PR.