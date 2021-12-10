<!-- Author: Jordan Randleman -:- COEN 160's Final Project Report -->

# Table of Contents
1. Running the Project (internet connection required!)
2. Brief Review & Implementation Motivation
3. Contribution
4. Code Overview (high-level)
5. Implementation Details (low-level)
6. Results
7. Conclusion



------------------------
# Running the Project
## An internet connection is required (more on this in `Implementation Details` below)!

## Compile the Interpreter
Within the `Scheme160` (named after COEN 160) directory, enter `javac Main.java`.

## Running the Project
Within the `Scheme160` directory, enter `java Main project.scm`.

## A Few More Notes
The syntax to launch the Scheme160 REPL is `java Main`.
The syntax to interpret a file is `java Main <filename.scm> <optional-argument> ...`.



------------------------
# Brief Review & Implementation Motivation
In what is likely one of the more creative approaches to tackling this final project, I've elected to implement an entire interpreter for a subset of the Scheme programming language in Java 11, then to implement the final project for COEN 160 in Scheme with support from a few unique Java 11 primitives, such that technically the entire project _is_ being executed by Java code that I wrote (just with the Scheme layer of indirection).

The idea for writing a Scheme interpreter in Java actually came about long before I intended to use such for this final project. As an engineering peer advisor and mentor, I had a new COEN freshman who was proficient in Java (having learned such both via AP classes and by practicing in their spare time), whom I thought would greatly benefit from learning how an interpreter works (I know I certainly did when I started implementing my own in C++ as a sophomore). 

Thing is, as I'd mentioned, the freshman was incredibly familiar with Java, and I'd only done big projects in JavaScript, C++, and C# before (my graduating class in highschool was 10 people, we were so small that we didn't have any AP classes or programming courses on campus, and hence I'd never had exposure to Java before). As such, the fact that I was finally learning Java by taking COEN 160 worked out perfectly! I took the opportunity to design a simple (and as elegant as possible) interpreter in order to be able to have a code base I could walk the freshman through in order to teach them about programming languages, while also being able to use it as an exercise for me to become better at Java (interpreters have a lot of moving parts, so they make for great projects to have you exposed to a variety of components of the implementation language being used).

As such, I did build out a fully-fledged interpreter for a subset of Scheme, the details of which are outlined in the project's `README.md` file. Furthermore, examples of demo scheme programs may be found in the `examples` folder.

However, as time went on and I needed to start work on the final project for this class, I thought it'd be cool to use such as a sort of "real test" for the interpreter. Having designed the interpreter in a modular fashion, I knew that all I'd need to do was add a set of supporting primitives in Java that Scheme code had access to in order to support operations like GUI manipulation and dictionary lookups. 

Hence the implementation style of choice found here! All of the Java primitives specific to the final project are found in `Primitive/Coen160Project.java`, the UML diagram for the project component (__not__ for the entire interpreter though) may be found in the `reportPictures` directory, and the Scheme source code for the final project itself may be found in `project.scm`. You'll note that the GUI itself is a bit bare-bones, but that's simply because the star of the show here is the Scheme interpretation being done rather than seeing how pretty I can make a pop-up window (it of course still satisfies the project requirements though!).

I had a __ton__ of fun with this project :) as well as with the process of adding things to the interpreter as I learned about them in class (think exception handling, synchronized access, file operations, etc.). Java is an amazing language, and one that I'm thrilled to continue using in my own personal projects going forward. 



------------------------
# Contribution
I wrote all of the code found in this project. As such, I won't be listing the modules out individually here, since everything is of my own work. Furthermore, I have attached a copy of the MIT license in order to ensure that anyone may use said code for their own purposes as well.



------------------------
# Code Overview (high-level)
First we shall review the code for the interpreter as a whole, then dive into the code specific to the final project. 

## Interpreter Overview
There are 3 directories of principle note here. These include `Util`, `Type`, and `Primitive`. `Main.java` simply dispatches to `Util/Core.java`, but acts as a convenient wrapper to compile and launch all of the interpreter's functions.

`Util` contains utility logic and support functionality for the interpreter. It houses the interpreter's lexer in `Reader.java`, runtime support in `Runtime.java`, a generic "pair" implementation in `Pair.java`, string parsing utilities in `StringParser.java`, a custom exception type for easier error reporting in `Exceptionf.java`, and the core Scheme evaluation logic in `Core.java`. A basic overview of the interpretation process is as such: a string of source code is lexed and converted into a data structure, which is then traversed by the core evaluator in order to perform certain actions based on what the evaluator sees in the given data structure (this is how any basic tree-walking interpreter works, including Scheme160).

`Type` contains the type classes supported by Scheme160. Like Python, Scheme programmers only deal with one dynamic type (the "Scheme object type" if you will), which reflects a set of distinct types under the hood. This dynamism is realized via polymorphism in Scheme160, wherein any class that extends `Type/Datum.java` may be used as an optional type value for Scheme objects. As such, every Scheme160 type (including numbers, strings, functions, etc.) __must__ extend the abstract `Datum` base class by implementing a variety of methods (including the type's truth, copying, and serialization semantics). `Type` also contains the `Primitive` functional interface that must be implemented by any Java primitive we want to have access to in our Scheme runtime.

`Primitive` contains the base set of Java primitives that the Scheme160 runtime has access to by default. Each Java primitive must implement the `Type/Primitive.java` functional interface and be registered in `Util/Runtime.java`. These are the functions we want implemented in Java either because they can't be defined directly in native Scheme (think of I/O operations), or because it would be more efficient to have them instead implemented in Java (think of the `sort` algorithm).

## Project Overview
The project is largely implemented in `project.scm`, with support from `Primitive/Coen160Project.java` in order to execute GUI operations and dictionary lookups via Java primitives. As such, `project.scm` is the one executing the control flow for the project, the validation and generation of letters, as well as the tracking of current input words and the current score. `Primitive/Coen160Project.java` provides the means by which to launch the initial window, relaunch a new game, get input from the GUI, and verify that a word exists according to a particular dictionary (more on this in `Implementation Details` below).



------------------------
# Implementation Details (low-level)

The GUI is implemented as a dance between 2 threads, the GUI thread and the main Scheme thread. Synchronized access between shared fields being read/written to by either thread (the "user word text field" and "reset button" states) is maintained by wrapping said fields in a class that has `synchronized` getters and setters. 

Dictionary lookups to verify whether a word exists are done via the free [https://dictionaryapi.dev](https://dictionaryapi.dev) API. As such, ___this project requires an internet connection in order to run!___ While certainly a down side on one hand (you can't play the game in an offline environment), it also affords us with a great benefit: the guarantee of contemporaneousness: whereas a static dictionary hardcoded into the project would only support the words of a single generation, an online dictionary will account for words being added/removed from our vernacular over time! This ensures that our game always appeals to those playing it by supporting new words as they are added to the online dictionary.

As stated earlier in the `Brief Review & Implementation Motivation` section above, the project's UML diagram may be found in the `reportPictures` directory. Note that since UML as we learned it works best for classes, and that Scheme does not support classes in the Java sense, I've accounted for such in my UML diagram by treating the entire `project.scm` Scheme file as one big class named `Project` with the functions within the file listed as methods of the `Project` class. 

Additionally I've made a few modifications to the project description (in keeping with the requirements still of course) in order to provide a better gaming experience. For one, I've added the rule that a word can only be entered once per game (note that the reset button also resets the "input words" list as well as the current score though). This is an intuitive addition, otherwise players could just continue entering in the same word over and over in order to rack up points. Additionally, in generating random letters for the user to spell with, I always guarantee that there is at least one vowel in their number (if only consonants were generated, I swap one of them out with the letter `e` since such is the most commonly used vowel \[and letter!] in the english language). Finally, I've made the letters case-insensitive, hence `DOG` and `dog` both resolve to the same word internally.

In terms of exactly how `project.scm` works, it launches the initial GUI window (which terminates the main thread upon exit), then launches a game loop. Each game loop is executed only by initiating the program upon launch, or by clicking the GUI's reset button. Upon initiation, the game loop generates a set of letters to be used, clears the set of input words, and sets the current score to 0. `gui-get-input`, a blocking primitive I implemented in Java, is then repeatedly called to check whether a word has been input or if the reset button was pressed on the GUI. If a word was input, the score is updated by the length of the word so long as it is composed of the given letters and is a new valid dictionary word, but if the reset button was pushed, a new game loop is initiated (scrapping the current input words and current score).

Note that in keeping with traditional Scheme fashion, `project.scm` itself is a purely functional program (never mutates any Scheme objects) :)

In implementing the interpreter, I came to appreciate the value of true object-oriented programming significantly more than I was able to when implementing an interpreter in C++. In C++, I implemented dynamic Scheme objects via a `union` under the hood, and initially, this was my approach in Java as well (though instead of a union I just had a set of fields in a single class). However, learning about the `instanceof` keyword was a lightbulb moment for me, and I realized that polymorphism was a __perfect__ use case for what I was trying to accomplish! This realization meant that I'd have to rewrite significant portions of the interpreter in order to implement such, but it was absolutely worth it :)

As a closing note, I'd highly encourage you to check out `project.scm` yourself :) note also that semicolons start single-line comments in Scheme, hence why the file looks as it does.



------------------------
# Results
Images of the GUI during a game may be found in the `reportPictures` directory. 

In terms of other results, I've gained an __immense__ appreciation for both Java and the JVM's pre-provided infrastructure. While my experience programming on a daily basis in C++ over the last few years have brought me great joy and learning, I'm eager to explore just what the JVM has to offer as I employ it in my future projects as well (I'm particularly excited to try out Clojure, a LISP similar to Scheme but that runs on the JVM and is more principled regarding concurrency and functional programming).



------------------------
# Conclusion

Java is awesome :) and I never want to see the word `pthread` again after tasting JVM concurrency.