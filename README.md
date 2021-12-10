<!-- Author: Jordan Randleman -:- Java 11 Scheme160 Interpreter's README -->

# Scheme160 (SCM160)
## Toy Interpreter For a Subset of Scheme Implemented in Java 11!
### Designed as an Educational Tool to Demystify Programming Languages



------------------------
# Using Scheme160
0. Compile the Interpreter: `javac Main.java`
1. Launch REPL: `java Main`
2. Interpret a File: `java Main <script-name> <optional-arg> ...`
3. Load a File into the REPL: `java Main -l <script-name> <optional-arg> ...`
   * `<optional-arg> ...` will populate the `*argv*` list as strings



------------------------
# Design Features
## Intentionally Simple
The Java code is meant to be reviewed & explained, and as such, the implementation has been kept as simple as possible.
This simplicity is achieved by forsaking a number of features found in other Scheme implementations:
* No continuations
* No multithreading
* A bare-bones macro system (no `syntax-rules` or `syntax-case`)
* No reader syntactic sugar (only s-expressions)
* No tail-call optimization
* No multi-line comments (only single-line)
* Minimal data structures (only lists & strings)

## Surprisingly Expressive
Despite the simplicity of the language, it can still preform some interesting operations!
Check out the files `/examples/` for some demo programs to try out.

## Designed to be Reviewed by Those at Least a Bit Familiar with Scheme
There are a number of fantastic Scheme/LISP tutorials on the internet, however, this is not one of them.
The interpreter, and this README, are designed only to show how one may implement a Scheme-esque langauge, **NOT** how one ought to use such a language.
If you're really interested, read SICP :)



------------------------
# Source File Layout

## Type
### Implements Scheme160 data type classes

0. Datum: Base Scheme Object Class (abstract)
1. Nil: The "empty list", `(quote ())`, only value for which `null?` is true
2. Boolean: `#t` is true and `#f` if false, only `#f` is falsey
3. Number: Java `double`s under the hood
4. Symbol: Value representations of symbolic literals (used in metaprograms)
5. String: Java `String`s under the hood
6. Pair: Pair of `Datum` objects (nested pairs ending in `Nil` are "proper lists")
7. Procedure: Base Scheme Procedure Class (abstract)
8. CompoundProcedure: User-defined procedure object
9. PrimitiveProcedure: Implementation-defined primitive procedure object wrapper
10. Environment: Hashmap to track variable bindings and parent scope
11. Primitive: Interface for implementation-defined primitives to implement
12. Macro: Meta-programming variant of `Procedure`, macro objects are created by `define-macro`
13. Void: The "empty object" returned by special operations like `define` and `set!`


------------------------
## Util
### Provides interpretation functionality and supplemental tooling

0. Core: Data structure evaluation logic
1. Reader: Source code string to data structure conversion logic
2. Runtime: Manages the call stack & global environment of the Scheme160 session
3. StringParser: Provides string escaping & unescaping functionality
4. Exceptionf: Exception wrapper to provide easier error formatting for Scheme160 primitives
5. Pair: Generic pair data structure


------------------------
## Primitive
### Provides Scheme160's Java 11 primitives (each of which implement the `Type.Primitive` interface)

0. Equality
1. Functional
2. IO
3. List
4. Number
5. Pair
6. Str
7. Symbol
8. Sys
9. TypeCoercion
10. TypePredicate
11. Utility



------------------------
# Supported Types
0. Number 
   * Java `double`s, hence `Infinity`, `-Infinity`, `NaN` are valid numbers too!
1. String
   * Denoted by double-quotes (nothing crazy)
2. Symbol 
   * Generate via the [`quote`](#quote) special form
3. Procedure
   * Generate via the [`lambda`](#lambda) special form
4. Macro
   * Generate via the [`define-macro`](#define-macro) special form
5. Bool
   * `#t` is true and `#f` if false (only `#f` is falsey)
6. Nil
   * The empty list, created via `(quote ())` or `(list)`
7. Pair 
   * Created by `cons`, and printed under an acyclic assumption!
8. Void
   * Returned by `define`, `set!`, `define-macro`, and `if`'s else branch w/o an alternative.



------------------------
# Special Forms Supported
### _Extensible via [`define-macro`](#define-macro)!_


## Define

#### Use: ___Bind a Syntactic Label to a Value!___

#### Forms:
```scheme
; Define a Variable
(define <variable-name> <value>)

; Define a Procedure
(define (<procedure-name> <parameter> ...) <body> ...)

; Alias <define> via <def>
(def life-universe-everything 42)
```

#### Procedure Definition Derivation:
```scheme
(define (<procedure-name> <parameter> ...) <body> ...)
; Becomes
(define <procedure-name> (lambda (<parameter> ...) <body> ...))
```


------------------------
## Set!

#### Use: ___Set a Syntactic Label to a New Value (must have already been defined)!___

#### Form: `(set! <variable-name> <new-value>)`


------------------------
## Define-macro

#### Use: ___Generates a Macro Object!___

#### Form: `(define-macro (<macro-name> <macro-expression-parameter>) <body> ...)`
* Macros operate similarly to procedures, EXCEPT they have their entire application 
  expression passed as a quoted list of data bound to `<macro-expression-parameter>`, 
  after which `<body> ...` is executed, the result of which is then passed to `eval`
  (which determines the final value of the macro application).

#### Example:
```scheme
; Implementing <delay> as a macro:
(define-macro (delay expr)
  (list (quote lambda) (quote ()) (cadr expr)))
```


------------------------
## Lambda

#### Use: ___Generates Anonymous Procedure!___

#### Form: `(lambda (<parameter> ...) <body> ...)`
* _Note: Pass a variadic number of args (0+) by using `.` as such:_
  - _Note: Variadic argument list's name must **always** be the last argument!_
    ```scheme
    (lambda (. <var-args-list>) <body> ...)         ; OK
    (lambda (<param> . <var-args-list>) <body> ...) ; OK
    (lambda (. <var-args-list> <param>) <body> ...) ; ERROR: <var-args-list> isn't last!
    ```


------------------------
## If

#### Use: ___Conditional Branching!___

#### Form: `(if <condition> <consequent> <optional-alternative>)`
* _Note: Use [`begin`](#begin) for multiple `<consequent>` and/or `<alternative>` expressions_


------------------------
## Begin

#### Use: ___Sequentially Evaluate Expressions (in the Current Environment Frame)!___
* Helps fit multiple expressions somewhere only expecting 1 (see [`if`](#if))

#### Form: `(begin <expression> ...)`


------------------------
## Quote

#### Use: ___Convert Code to Data!___

#### Quoting a Datum:
* Proper List: `(quote (<obj> ...))` => `(list (quote <obj>) (quote ...))`
* Pair: `(quote (<car-obj1> . <cdr-obj>))` => `(cons (quote <car-obj1>) (quote <cdr-obj>))`
* Nil: `(quote ())` _(unique value, ONLY one returning `#t` for `null?`)_
* Syntax: `(quote <syntax>)` => `<syntax-as-symbol>`
* Else: `(quote <any-other-obj>)` => `<any-other-obj>`

#### Examples:
```scheme
(quote 12)             ; => 12
(quote hello)          ; => hello
(quote (1 2 3))        ; => (list 1 2 3)
(quote (quote double)) ; => (list (quote quote) (quote double))
(quote (define a 12))  ; => (list (quote define) (quote a) (quote 12))
```


------------------------
## And

#### Use: ___Serves as Scheme's Boolean AND Operation___
* Think `&&` in C++/Java

#### Form: `(and <obj> ...)`

#### Defined as a Macro:
```scheme
(define-macro (and expr)
  (fold (lambda (acc item) (list (quote if) acc item #f))
        #t
        (cdr expr)))
```


------------------------
## Or

#### Use: ___Serves as Scheme's Boolean OR Operation___
* Think `||` in C++/Java

#### Form: `(or <obj> ...)`

#### Defined as a Macro:
```scheme
(define-macro (or expr)
  (fold-right (lambda (item acc) 
                (list (list (quote lambda) (list (quote scm160:or-value))
                            (list (quote if) (quote scm160:or-value) (quote scm160:or-value) acc)) 
                      item))
              #f
              (cdr expr)))
```


------------------------
## Delay

#### Use: ___Delay an Expression's Evaluation by Creating a Promise!___
* _Force the promise to run its expression via the `force` primitive!_

#### Form: `(delay <expression>)`

#### Defined as a Macro:
```scheme
(define-macro (delay expr)
  (list (quote lambda) (quote ()) (cadr expr)))
```


------------------------
## Cond

#### Use: ___Concise If-Else Chains!___

#### Form: `(cond <clause1> <clause2> ...)`, `<clause>` = `(<condition> <exp1> <exp2> ...)`
* _Using `else` as the condition of the last clause is equivalent to using `#t` as the condition_

#### Defined as a Macro:
```scheme
(define-macro (cond expr)
  (define (make-condition c) (if (eq? c (quote else)) #t c))
  (define (make-consequence c) (cons (quote begin) c))
  (fold-right (lambda (clause acc)
                (list (quote if) (make-condition (car clause))
                      (make-consequence (cdr clause))
                      acc))
              (quote (if #f #f)) ; innermost expr yields a <void> value
              (cdr expr)))
```


------------------------
## Let

#### Use: ___Temporary Bindings in a New Scope!___

#### Forms:
0. Nameless: `(let ((<variable> <value>) ...) <body> ...)`
1. Named: `(let <procedure-name> ((<parameter> <initial-value>)) <body> ...)`

#### Defined as a Macro:
```scheme
(define-macro (let expr)
  (define (get-params let-bindings) (map car let-bindings))
  (define (get-args let-bindings) (map cadr let-bindings))
  (define (get-body let-body) (cons (quote begin) let-body))
  (define (generate-anon-let)
    (cons (list (quote lambda) (get-params (cadr expr))
                (get-body (cddr expr)))
          (get-args (cadr expr))))
  (define (generate-named-let)
    (list (list (quote lambda) (quote ())
            (list (quote begin)
              (list (quote define) (cadr expr)
                    (list (quote lambda) (get-params (car (cddr expr)))
                          (get-body (cdr (cddr expr)))))
              (cons (cadr expr) (get-args (car (cddr expr))))))))
  (if (symbol? (cadr expr))
      (generate-named-let)
      (generate-anon-let)))
```


------------------------
## Quasiquote, Unquote, & Unquote-Splicing

#### Use: ___Selectively Eval & Convert Code to Data!___

#### Quoting a Datum (exactly like [`quote`](#quote), with 2 key exceptions):
0. `unquote`ing data undoes the quotation done by `quasiquote`
1. `unquote-splicing` = `unquote` _and_ "unwraps" parenthesis
   * Hence result of `unquote-splicing` **must** eval to an acyclic list

#### Examples:
```scheme
(define a 12)
(quasiquote (a a))           ; => (list (quote a) (quote a))
(quasiquote (a (unquote a))) ; => (list (quote a) 12)

(define b (quote (1 2 3)))
(quasiquote (b b))                                       ; => (list (quote b) (quote b))
(quasiquote ((unquote b) (unquote b)))                   ; => (list (list 1 2 3) (list 1 2 3))
(quasiquote ((unquote-splicing b) (unquote-splicing b))) ; => (list 1 2 3 1 2 3)

(define c (cons 3 4))
(quasiquote (1 2 (unquote c)))          ; => (quote (1 2 (3 . 4)))
(quasiquote (1 (unquote c) 2))          ; => (quote (1 (3 . 4) 2))
(quasiquote (1 2 (unquote-splicing c))) ; => (quote (1 2 3 . 4))
(quasiquote (1 (unquote-splicing c) 2)) ; => ERROR! CANT APPEND 2 TO DOTTED LIST (quote (1 3 . 4))
```

#### Defined as a Macro:
```scheme
(define (scm160:quasiquote:tagged-list? obj tag)
  (and (eq? (car obj) tag) (not (null? (cdr obj)))))

(define (scm160:quasiquote->quote lst level)
  (define (iter lst)
    (define hd (if (not (atom? lst)) (car lst)))
          ; finished parsing expression (proper list)
    (cond ((null? lst) (quote ()))
          ; finished parsing expression (dotted list)
          ((atom? lst)
            (list (list (quote quote) lst)))
          ; unquote rest of list
          ((scm160:quasiquote:tagged-list? lst (quote unquote))
            (if (= level 0)
                (list (cadr lst))
                (list (list (quote list) (quote (quote unquote)) (scm160:quasiquote->quote (cadr lst) (- level 1)))))) ; *there*: recursively parse, in nested quasiquote
          ; quote atom
          ((atom? hd)
            (cons (list (quote list) (list (quote quote) hd))
                  (iter (cdr lst))))
          ; unquote datum
          ((scm160:quasiquote:tagged-list? hd (quote unquote))
            (if (= level 0)
                (cons (list (quote list) (cadr hd))
                      (iter (cdr lst)))
                (cons (list (quote list) (scm160:quasiquote->quote hd level)) ; recursively parse, in nested quasiquote (level will be decremented *there*)
                      (iter (cdr lst)))))
          ; unquote & signal should splice element
          ((scm160:quasiquote:tagged-list? hd (quote unquote-splicing))
            (if (= level 0)
                (cons (cadr hd) ; evaluate datum & append to the expression
                      (iter (cdr lst)))
                (cons (list (quote list) (scm160:quasiquote->quote hd (- level 1))) ; recursively parse, in nested quasiquote
                      (iter (cdr lst)))))
          ; nested quasiquote
          ((scm160:quasiquote:tagged-list? hd (quote quasiquote))
            (cons (list (quote list) (scm160:quasiquote->quote hd (+ level 1))) ; recursively parse, in nested quasiquote
                  (iter (cdr lst))))
          ; quasiquote expression
          (else
            (cons (list (quote list) (scm160:quasiquote->quote hd level))
                  (iter (cdr lst))))))
  (cons (quote append) (iter lst)))

(define-macro (quasiquote expr)
  (scm160:quasiquote->quote (cadr expr) 0))
```



------------------------
# Primitive Procedures
### Functions implemented in Java accessible by Scheme160 source code!

## Numbers:
```scheme
(= <num> <num> ...)
(< <num> <num> ...)
(> <num> <num> ...)
(<= <num> <num> ...)
(>= <num> <num> ...)

(+ <num> <num> ...)
(- <num> <num> ...) (- <num>)
(/ <num> <num> ...) (/ <num>)
(* <num> <num> ...)

(expt <num> <num> ...)
(exp <num>)
(log <num>)
(sqrt <num>)
(abs <num>)

(quotient <num> <num>)
(remainder <num> <num>)

(round <num>)
(floor <num>)
(ceiling <num>)
(truncate <num>)

(min <num> ...)
(max <num> ...)

(number? <obj>)
(integer? <num>)
(finite? <num>)
(infinite? <num>)
(nan? <num>)

(odd? <num>)
(even? <num>)

(positive? <num>)
(negative? <num>)
(zero? <num>)

(sin <num>)
(cos <num>)
(tan <num>)
(asin <num>)
(acos <num>)
(atan <num>) (atan <num> <num>)
(sinh <num>)
(cosh <num>)
(tanh <num>)
(asinh <num>)
(acosh <num>)
(atanh <num>)

(random) ; random number between 0.0 and 1.0
```


------------------------
## IO:
```scheme
(write <obj>)
(display <obj>)
(newline)
(read)
(read-string <str>) ; returns a pair: (cons <read-datum> <str-without-serialized-read-datum>)
```


------------------------
## System:
```scheme
(exit)

(file-read <filename-str>) ; read file contents as a data structure
(file-read-string <filename-str>) ; read file contents as a string

(file-write <filename-str> <obj>)
(file-display <filename-str> <obj>)

(file-delete! <filename-str>)

(file? <str>)

(load <filename-str>)
```


------------------------
## Pairs:
```scheme
(cons <obj> <obj>)

(car <pair>)
(cdr <pair>)
(caar <pair>)
(cadr <pair>)
(cdar <pair>)
(cddr <pair>)

(set-car! <pair> <obj>)
(set-cdr! <pair> <obj>)

(pair? <obj>)
(atom? <obj>) ; equivalent to (not (pair? <obj>))
```


------------------------
## Lists:
```scheme
(list <obj> ...) (list)
(list* <obj> <obj> ...) ; create a dotted list
(append <list> ... <obj>)

(length <list>)

(reverse <list>)

(map <procedure> <list> ...)
(filter <predicate?> <list>)
(for-each <procedure> <list> ...)

(fold <procedure> <seed-obj> <list> ...)
(fold-right <procedure> <seed-obj> <list> ...)

(last <list>)
(init <list>)
(ref <list> <index-num>)
(sublist <list> <index-num> <optional-length-num>) ; length defaults to end of list

(memq <obj> <list>)
(member <obj> <list>)

(assq <key-obj> <alist>)
(assoc <key-obj> <alist>)

(sort <predicate?> <list>)
(sorted? <predicate?> <list>)

(list? <obj>)
(list*? <obj>)
(circular-list? <obj>)
(alist? <obj>) ; associative list predicate
(null? <obj>)
```


------------------------
## Equality:
```scheme
(eq? <obj> <obj> ...)
(equal? <obj> <obj> ...)
```


------------------------
## Type Predicates:
```scheme
(typeof <obj>) ; returns a symbol of obj's typename
(void? <obj>)
(boolean? <obj>)
```


------------------------
## Type Coercions:
```scheme
(number->string <num>)
(string->number <str>)
(write-to-string <obj>)
(display-to-string <obj>)
```


------------------------
## Strings:
```scheme
(string-length <str>)
(string-empty? <str>)

(string-reverse <str>)

(string-append <str> ...)

(string-ref <str> <index-num>) ; returns a substring of length 1 (since no characters)
(substring <str> <index-num> <optional-length-num>)  ; length defaults to end of string

(string-upcase <str>)
(string-downcase <str>)

(string-replace <str> <regex-str> <replacement-str>)
(string-trim <str>)

(string-contains <str> <str>) ; returns index of appearance or #f if not present
(string-contains-right <str> <str>) ; returns index of appearance or #f if not present

(string-join <string-list> <optional-str>) ; str defaults to ""
(string-split <str> <regex-str>)

(string=? <str> <str> ...)
(string<? <str> <str> ...)
(string>? <str> <str> ...)
(string<=? <str> <str> ...)
(string>=? <str> <str> ...)

(string-ci=? <str> <str> ...)
(string-ci<? <str> <str> ...)
(string-ci>? <str> <str> ...)
(string-ci<=? <str> <str> ...)
(string-ci>=? <str> <str> ...)

(string? <obj>)
```


------------------------
## Symbols:
```scheme
(symbol-append <sym> ...)
(symbol? <obj>)
```


------------------------
## Utilities:
```scheme
(not <obj>)
(force <delayed-expression>)
(apply <procedure> <obj-argument-list>)
(eval <quoted-expression>)
(copy <obj>)
```


------------------------
## Functional:
```scheme
(compose <procedure> ...)
(bind <procedure> <obj> ...) ; bind <obj> ... as arguments to <procedure>
(id <obj>)
(procedure? <obj>)
```
