// Author: Jordan Randleman - Util.Runtime
// Purpose:
//    Scheme runtime support. Provides the functionality to initialize the global 
//    environment with Java Primitives, maintain the callstack for easier debugging,
//    keep track of the global environment pointer, whether we're in the REPL (i.e. 
//    whether we should print an exit message), and the session's pseudo random number 
//    generator (PRNG).

package Util;
import Type.Environment;
import java.util.ArrayList;
import java.util.Random;

public class Runtime {
  ////////////////////////////////////////////////////////////////////////////
  // Representing a Call Stack (used exclusively for tracing)
  public static class CallStack {
    private static ArrayList<String> callStack = new ArrayList<String>();

    public static void push(String prcocedureName) {
      callStack.add(prcocedureName);
    }

    public static void pop() {
      callStack.remove(callStack.size()-1);
    }

    public static void printTrace() {
      int n = callStack.size();
      if(n == 0) return;
      System.err.printf("SCM160 CALL STACK: %s\n", callStack.get(0));
      for(int i = 1; i < n; ++i)
        System.err.printf("                   %s\n", callStack.get(i));
    }

    public static void reset() {
      callStack = new ArrayList<String>();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // Track if currently in a REPL session (determines if exit msg is printed)
  public static boolean inREPL = false;


  ////////////////////////////////////////////////////////////////////////////
  // Global Random Number Generator
  public static Random prng = new Random();


  ////////////////////////////////////////////////////////////////////////////
  // Representing the Global Environment
  // => Initialized via <initialize> below
  public static Environment globalEnvironment = new Environment();


  ////////////////////////////////////////////////////////////////////////////
  // Registering Scheme160 primitives written in Java
  private static void registerPrimitives() throws Exception {
    // Register Numeric Primitives
    globalEnvironment.define("+",new Primitive.Number.Plus());
    globalEnvironment.define("-",new Primitive.Number.Minus());
    globalEnvironment.define("*",new Primitive.Number.Multiply());
    globalEnvironment.define("/",new Primitive.Number.Divide());

    globalEnvironment.define("=",new Primitive.Number.Equals());
    globalEnvironment.define("<",new Primitive.Number.LessThan());
    globalEnvironment.define(">",new Primitive.Number.GreaterThan());
    globalEnvironment.define("<=",new Primitive.Number.LessThanOrEqualTo());
    globalEnvironment.define(">=",new Primitive.Number.GreaterThanOrEqualTo());

    globalEnvironment.define("expt",new Primitive.Number.Expt());
    globalEnvironment.define("exp",new Primitive.Number.Exp());
    globalEnvironment.define("log",new Primitive.Number.Log());
    globalEnvironment.define("sqrt",new Primitive.Number.Sqrt());
    globalEnvironment.define("abs",new Primitive.Number.Abs());

    globalEnvironment.define("min",new Primitive.Number.Min());
    globalEnvironment.define("max",new Primitive.Number.Max());

    globalEnvironment.define("quotient",new Primitive.Number.Quotient());
    globalEnvironment.define("remainder",new Primitive.Number.Remainder());

    globalEnvironment.define("round",new Primitive.Number.Round());
    globalEnvironment.define("floor",new Primitive.Number.Floor());
    globalEnvironment.define("ceiling",new Primitive.Number.Ceiling());
    globalEnvironment.define("truncate",new Primitive.Number.Truncate());

    globalEnvironment.define("number?",new Primitive.Number.IsNumber());
    globalEnvironment.define("integer?",new Primitive.Number.IsInteger());
    globalEnvironment.define("finite?",new Primitive.Number.IsFinite());
    globalEnvironment.define("infinite?",new Primitive.Number.IsInfinite());
    globalEnvironment.define("nan?",new Primitive.Number.IsNaN());

    globalEnvironment.define("odd?",new Primitive.Number.IsOdd());
    globalEnvironment.define("even?",new Primitive.Number.IsEven());

    globalEnvironment.define("positive?",new Primitive.Number.IsPositive());
    globalEnvironment.define("negative?",new Primitive.Number.IsNegative());
    globalEnvironment.define("zero?",new Primitive.Number.IsZero());

    globalEnvironment.define("sin",new Primitive.Number.Sin());
    globalEnvironment.define("cos",new Primitive.Number.Cos());
    globalEnvironment.define("tan",new Primitive.Number.Tan());
    globalEnvironment.define("asin",new Primitive.Number.Asin());
    globalEnvironment.define("acos",new Primitive.Number.Acos());
    globalEnvironment.define("atan",new Primitive.Number.Atan());
    globalEnvironment.define("sinh",new Primitive.Number.Sinh());
    globalEnvironment.define("cosh",new Primitive.Number.Cosh());
    globalEnvironment.define("tanh",new Primitive.Number.Tanh());
    globalEnvironment.define("asinh",new Primitive.Number.Asinh());
    globalEnvironment.define("acosh",new Primitive.Number.Acosh());
    globalEnvironment.define("atanh",new Primitive.Number.Atanh());

    globalEnvironment.define("random",new Primitive.Number.Random());


    // Register IO Primitives
    globalEnvironment.define("write",new Primitive.IO.Write());
    globalEnvironment.define("display",new Primitive.IO.Display());
    globalEnvironment.define("newline",new Primitive.IO.Newline());
    globalEnvironment.define("read",new Primitive.IO.Read());
    globalEnvironment.define("read-string",new Primitive.IO.ReadString());


    // Register System Primitives
    globalEnvironment.define("*argv*",Primitive.Sys.argv);

    globalEnvironment.define("exit",new Primitive.Sys.Exit());

    globalEnvironment.define("file-read",new Primitive.Sys.FileRead());
    globalEnvironment.define("file-read-string",new Primitive.Sys.FileReadString());

    globalEnvironment.define("file-write",new Primitive.Sys.FileWrite());
    globalEnvironment.define("file-display",new Primitive.Sys.FileDisplay());

    globalEnvironment.define("file-delete!",new Primitive.Sys.FileDelete());

    globalEnvironment.define("file?",new Primitive.Sys.IsFile());

    globalEnvironment.define("load",new Primitive.Sys.Load());


    // Register Pair Primitives
    globalEnvironment.define("cons",new Primitive.Pair.Cons());

    globalEnvironment.define("car",new Primitive.Pair.Car());
    globalEnvironment.define("cdr",new Primitive.Pair.Cdr());
    globalEnvironment.define("caar",new Primitive.Pair.Caar());
    globalEnvironment.define("cadr",new Primitive.Pair.Cadr());
    globalEnvironment.define("cdar",new Primitive.Pair.Cdar());
    globalEnvironment.define("cddr",new Primitive.Pair.Cddr());

    globalEnvironment.define("set-car!",new Primitive.Pair.SetCar());
    globalEnvironment.define("set-cdr!",new Primitive.Pair.SetCdr());

    globalEnvironment.define("pair?",new Primitive.Pair.IsPair());
    globalEnvironment.define("atom?",new Primitive.Pair.IsAtom());


    // Register List Primitives
    globalEnvironment.define("list",new Primitive.List.ConstructList());
    globalEnvironment.define("list*",new Primitive.List.ListStar());
    globalEnvironment.define("append",new Primitive.List.Append());

    globalEnvironment.define("length",new Primitive.List.Length());

    globalEnvironment.define("reverse",new Primitive.List.Reverse());

    globalEnvironment.define("map",new Primitive.List.Map());
    globalEnvironment.define("for-each",new Primitive.List.ForEach());
    globalEnvironment.define("filter",new Primitive.List.Filter());

    globalEnvironment.define("fold",new Primitive.List.Fold());
    globalEnvironment.define("fold-right",new Primitive.List.FoldRight());

    globalEnvironment.define("last",new Primitive.List.Last());
    globalEnvironment.define("init",new Primitive.List.Init());
    globalEnvironment.define("ref",new Primitive.List.Ref());
    globalEnvironment.define("sublist",new Primitive.List.Sublist());

    globalEnvironment.define("memq",new Primitive.List.Memq());
    globalEnvironment.define("member",new Primitive.List.Member());

    globalEnvironment.define("assq",new Primitive.List.Assq());
    globalEnvironment.define("assoc",new Primitive.List.Assoc());

    globalEnvironment.define("sort",new Primitive.List.Sort());
    globalEnvironment.define("sorted?",new Primitive.List.IsSorted());

    globalEnvironment.define("list?",new Primitive.List.IsList());
    globalEnvironment.define("list*?",new Primitive.List.IsListStar());
    globalEnvironment.define("circular-list?",new Primitive.List.IsCircularList());
    globalEnvironment.define("alist?",new Primitive.List.IsAlist());
    globalEnvironment.define("null?",new Primitive.List.IsNull());

    // Register Equality Primitives
    globalEnvironment.define("eq?",new Primitive.Equality.IsEq());
    globalEnvironment.define("equal?",new Primitive.Equality.IsEqual());

    // Register Type Predicate Primitives
    globalEnvironment.define("typeof",new Primitive.TypePredicate.Typeof()); 
    globalEnvironment.define("void?",new Primitive.TypePredicate.IsVoid()); 
    globalEnvironment.define("boolean?",new Primitive.TypePredicate.IsBoolean());

    // Register Type Coercion Primitives
    globalEnvironment.define("string->number",new Primitive.TypeCoercion.StringToNumber()); 
    globalEnvironment.define("number->string",new Primitive.TypeCoercion.NumberToString()); 
    globalEnvironment.define("write-to-string",new Primitive.TypeCoercion.WriteToString()); 
    globalEnvironment.define("display-to-string",new Primitive.TypeCoercion.DisplayToString()); 

    // Register String Primitives
    globalEnvironment.define("string-length",new Primitive.Str.StringLength()); 
    globalEnvironment.define("string-empty?",new Primitive.Str.IsStringEmpty());

    globalEnvironment.define("string-reverse",new Primitive.Str.StringReverse());

    globalEnvironment.define("string-append",new Primitive.Str.StringAppend());

    globalEnvironment.define("string-ref",new Primitive.Str.StringRef());
    globalEnvironment.define("substring",new Primitive.Str.Substring());

    globalEnvironment.define("string-upcase",new Primitive.Str.StringUpcase());
    globalEnvironment.define("string-downcase",new Primitive.Str.StringDowncase());

    globalEnvironment.define("string-replace",new Primitive.Str.StringReplace());
    globalEnvironment.define("string-trim",new Primitive.Str.StringTrim());

    globalEnvironment.define("string-contains",new Primitive.Str.StringContains());
    globalEnvironment.define("string-contains-right",new Primitive.Str.StringContainsRight());

    globalEnvironment.define("string-join",new Primitive.Str.StringJoin());
    globalEnvironment.define("string-split",new Primitive.Str.StringSplit());

    globalEnvironment.define("string=?",new Primitive.Str.StringEquals());
    globalEnvironment.define("string<?",new Primitive.Str.StringLessThan());
    globalEnvironment.define("string>?",new Primitive.Str.StringGreaterThan());
    globalEnvironment.define("string<=?",new Primitive.Str.StringLessThanOrEqualTo());
    globalEnvironment.define("string>=?",new Primitive.Str.StringGreaterThanOrEqualTo());

    globalEnvironment.define("string-ci=?",new Primitive.Str.StringCiEquals());
    globalEnvironment.define("string-ci<?",new Primitive.Str.StringCiLessThan());
    globalEnvironment.define("string-ci>?",new Primitive.Str.StringCiGreaterThan());
    globalEnvironment.define("string-ci<=?",new Primitive.Str.StringCiLessThanOrEqualTo());
    globalEnvironment.define("string-ci>=?",new Primitive.Str.StringCiGreaterThanOrEqualTo());

    globalEnvironment.define("string?",new Primitive.Str.IsString());

    // Register Symbol Primitives
    globalEnvironment.define("symbol-append",new Primitive.Symbol.SymbolAppend()); 
    globalEnvironment.define("symbol?",new Primitive.Symbol.IsSymbol());

    // Register Utility Primitives
    globalEnvironment.define("not",new Primitive.Utility.Not());
    globalEnvironment.define("force",new Primitive.Utility.Force());
    globalEnvironment.define("apply",new Primitive.Utility.Apply());
    globalEnvironment.define("eval",new Primitive.Utility.Eval());
    globalEnvironment.define("copy",new Primitive.Utility.Copy());

    // Register Functional Primitives
    globalEnvironment.define("compose",new Primitive.Functional.Compose());
    globalEnvironment.define("bind",new Primitive.Functional.Bind());
    globalEnvironment.define("id",new Primitive.Functional.Id());
    globalEnvironment.define("procedure?",new Primitive.Functional.IsProcedure());

    // Register COEN160 Project Primitives
    globalEnvironment.define("gui-get-input",new Primitive.Coen160Project.GuiGetInput());
    globalEnvironment.define("gui-launch-session",new Primitive.Coen160Project.GuiLaunchSession());
    globalEnvironment.define("gui-launch-window",new Primitive.Coen160Project.GuiLaunchWindow());
    globalEnvironment.define("dictionary-valid-word?",new Primitive.Coen160Project.DictionaryIsValidWord());
  }


  ////////////////////////////////////////////////////////////////////////////
  // Registering Scheme160 macros implemented in Scheme160
  private static void registerMacros() throws Exception {
    StringBuilder macros = new StringBuilder();
    macros.append(";; Macros to define custom special forms in Scheme160\n")
          .append(";;   => Hardcoded in ../Util/Runtime as a series of \".append\" statements\n")
          .append("")
          .append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n")
          .append(";; Implementing Boolean AND:  (and <obj> ...)\n")
          .append("(define-macro (and expr)\n")
          .append("  (fold (lambda (acc item) (list (quote if) acc item #f))\n")
          .append("        #t\n")
          .append("        (cdr expr)))\n")
          .append("")
          .append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n")
          .append(";; Implementing Boolean OR: (or <obj> ...)\n")
          .append("(define-macro (or expr)\n")
          .append("  (fold-right (lambda (item acc)\n")
          .append("                (list (list (quote lambda) (list (quote scm160:or-value))\n")
          .append("                            (list (quote if) (quote scm160:or-value) (quote scm160:or-value) acc))\n")
          .append("                      item))\n")
          .append("              #f\n")
          .append("              (cdr expr)))\n")
          .append("")
          .append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n")
          .append(";; Implementing DELAY: (delay <obj>)\n")
          .append("(define-macro (delay expr)\n")
          .append("  (list (quote lambda) (quote ()) (cadr expr)))\n")
          .append("")
          .append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n")
          .append(";; Implementing COND: (cond (<condition> <expr> ...) ...) (cond (<condition> <expr> ...) ... (else <expr> ...))\n")
          .append("(define-macro (cond expr)\n")
          .append("  (define (make-condition c) (if (eq? c (quote else)) #t c))\n")
          .append("  (define (make-consequence c) (cons (quote begin) c))\n")
          .append("  (fold-right (lambda (clause acc)\n")
          .append("                (list (quote if) (make-condition (car clause))\n")
          .append("                      (make-consequence (cdr clause))\n")
          .append("                      acc))\n")
          .append("              (quote (if #f #f)) ; innermost expr yields a <void> value\n")
          .append("              (cdr expr)))\n")
          .append("")
          .append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n")
          .append(";; Implementing LET: (let ((<variable> <value>) ...) <body> ...) (let <procedure-name> ((<argument> <initial-value>) ...) <body> ...)\n")
          .append("(define-macro (let expr)\n")
          .append("  (define (get-params let-bindings) (map car let-bindings))\n")
          .append("  (define (get-args let-bindings) (map cadr let-bindings))\n")
          .append("  (define (get-body let-body) (cons (quote begin) let-body))\n")
          .append("  (define (generate-anon-let)\n")
          .append("    (cons (list (quote lambda) (get-params (cadr expr))\n")
          .append("                (get-body (cddr expr)))\n")
          .append("          (get-args (cadr expr))))\n")
          .append("  (define (generate-named-let)\n")
          .append("    (list (list (quote lambda) (quote ())\n")
          .append("            (list (quote begin)\n")
          .append("              (list (quote define) (cadr expr)\n")
          .append("                    (list (quote lambda) (get-params (car (cddr expr)))\n")
          .append("                          (get-body (cdr (cddr expr)))))\n")
          .append("              (cons (cadr expr) (get-args (car (cddr expr))))))))\n")
          .append("  (if (symbol? (cadr expr))\n")
          .append("      (generate-named-let)\n")
          .append("      (generate-anon-let)))\n")
          .append("")
          .append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n")
          .append(";; Implementing QUASIQUOTE: (quasiquote <obj>) (unquote <obj>) (unquote-splicing <obj>)\n")
          .append("(define (scm160:quasiquote:tagged-list? obj tag)\n")
          .append("  (and (eq? (car obj) tag) (not (null? (cdr obj)))))\n")
          .append("")
          .append("(define (scm160:quasiquote->quote lst level)\n")
          .append("  (define (iter lst)\n")
          .append("    (define hd (if (not (atom? lst)) (car lst)))\n")
          .append("          ; finished parsing expression (proper list)\n")
          .append("    (cond ((null? lst) (quote ()))\n")
          .append("          ; finished parsing expression (dotted list)\n")
          .append("          ((atom? lst)\n")
          .append("            (list (list (quote quote) lst)))\n")
          .append("          ; unquote rest of list\n")
          .append("          ((scm160:quasiquote:tagged-list? lst (quote unquote))\n")
          .append("            (if (= level 0)\n")
          .append("                (list (cadr lst))\n")
          .append("                (list (list (quote list) (quote (quote unquote)) (scm160:quasiquote->quote (cadr lst) (- level 1)))))) ; *there*: recursively parse, in nested quasiquote\n")
          .append("          ; quote atom\n")
          .append("          ((atom? hd)\n")
          .append("            (cons (list (quote list) (list (quote quote) hd))\n")
          .append("                  (iter (cdr lst))))\n")
          .append("          ; unquote datum\n")
          .append("          ((scm160:quasiquote:tagged-list? hd (quote unquote))\n")
          .append("            (if (= level 0)\n")
          .append("                (cons (list (quote list) (cadr hd))\n")
          .append("                      (iter (cdr lst)))\n")
          .append("                (cons (list (quote list) (scm160:quasiquote->quote hd level)) ; recursively parse, in nested quasiquote (level will be decremented *there*)\n")
          .append("                      (iter (cdr lst)))))\n")
          .append("          ; unquote & signal should splice element\n")
          .append("          ((scm160:quasiquote:tagged-list? hd (quote unquote-splicing))\n")
          .append("            (if (= level 0)\n")
          .append("                (cons (cadr hd) ; evaluate datum & append to the expression\n")
          .append("                      (iter (cdr lst)))\n")
          .append("                (cons (list (quote list) (scm160:quasiquote->quote hd (- level 1))) ; recursively parse, in nested quasiquote\n")
          .append("                      (iter (cdr lst)))))\n")
          .append("          ; nested quasiquote\n")
          .append("          ((scm160:quasiquote:tagged-list? hd (quote quasiquote))\n")
          .append("            (cons (list (quote list) (scm160:quasiquote->quote hd (+ level 1))) ; recursively parse, in nested quasiquote\n")
          .append("                  (iter (cdr lst))))\n")
          .append("          ; quasiquote expression\n")
          .append("          (else\n")
          .append("            (cons (list (quote list) (scm160:quasiquote->quote hd level))\n")
          .append("                  (iter (cdr lst))))))\n")
          .append("  (cons (quote append) (iter lst)))\n")
          .append("")
          .append("(define-macro (quasiquote expr)\n")
          .append("  (scm160:quasiquote->quote (cadr expr) 0))\n");
    Core.eval(globalEnvironment,Primitive.Sys.FileRead.readBuffer(macros.toString()));
  }


  ////////////////////////////////////////////////////////////////////////////
  // Initialize the global environment
  public static void initialize() throws Exception {
    registerPrimitives();
    registerMacros();
  }
}