// Author: Jordan Randleman - Util.Core
// Purpose:
//    Core Scheme evaluator -- given an environment and an expression data structure,
//    "Core.eval" will evaluate that expression using the given environment's bindings.
//
//    Note that "Core.eval" is effectively a dispatch mechanism serving to resolve symbols,
//    reflect atomics, evaluate special forms, and apply procedures.
//
//    Further note that this file also contains the logic to launch the Scheme REPL and 
//    load a Scheme script.

package Util;
import Type.Datum;
import Type.Environment;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Core {
  ////////////////////////////////////////////////////////////////////////////
  // Helper(s)
  private static boolean isTaggedList(Type.Pair p, String tag) throws Exception {
    return p.car instanceof Type.Symbol && ((Type.Symbol)p.car).value.equals(tag);
  }

  public static Datum car(Datum d) throws Exception {
    if(!(d instanceof Type.Pair))
      throw new Exceptionf("Invalid 'car in evaluator: %s isn't a pair!", d.write());
    return ((Type.Pair)d).car;
  }

  public static Datum cdr(Datum d) throws Exception {
    if(!(d instanceof Type.Pair))
      throw new Exceptionf("Invalid 'cdr in evaluator: %s isn't a pair!", d.write());
    return ((Type.Pair)d).cdr;
  }

  public static Datum caar(Datum d) throws Exception { return car(car(d)); }
  public static Datum cadr(Datum d) throws Exception { return car(cdr(d)); }
  public static Datum cdar(Datum d) throws Exception { return cdr(car(d)); }
  public static Datum cddr(Datum d) throws Exception { return cdr(cdr(d)); }

  public static Datum caaar(Datum d) throws Exception { return car(car(car(d))); }
  public static Datum caadr(Datum d) throws Exception { return car(car(cdr(d))); }
  public static Datum cadar(Datum d) throws Exception { return car(cdr(car(d))); }
  public static Datum caddr(Datum d) throws Exception { return car(cdr(cdr(d))); }
  public static Datum cdaar(Datum d) throws Exception { return cdr(car(car(d))); }
  public static Datum cdadr(Datum d) throws Exception { return cdr(car(cdr(d))); }
  public static Datum cddar(Datum d) throws Exception { return cdr(cdr(car(d))); }
  public static Datum cdddr(Datum d) throws Exception { return cdr(cdr(cdr(d))); }

  public static Datum caaaar(Datum d) throws Exception { return car(car(car(car(d)))); }
  public static Datum caaadr(Datum d) throws Exception { return car(car(car(cdr(d)))); }
  public static Datum caadar(Datum d) throws Exception { return car(car(cdr(car(d)))); }
  public static Datum caaddr(Datum d) throws Exception { return car(car(cdr(cdr(d)))); }
  public static Datum cadaar(Datum d) throws Exception { return car(cdr(car(car(d)))); }
  public static Datum cadadr(Datum d) throws Exception { return car(cdr(car(cdr(d)))); }
  public static Datum caddar(Datum d) throws Exception { return car(cdr(cdr(car(d)))); }
  public static Datum cadddr(Datum d) throws Exception { return car(cdr(cdr(cdr(d)))); }
  public static Datum cdaaar(Datum d) throws Exception { return cdr(car(car(car(d)))); }
  public static Datum cdaadr(Datum d) throws Exception { return cdr(car(car(cdr(d)))); }
  public static Datum cdadar(Datum d) throws Exception { return cdr(car(cdr(car(d)))); }
  public static Datum cdaddr(Datum d) throws Exception { return cdr(car(cdr(cdr(d)))); }
  public static Datum cddaar(Datum d) throws Exception { return cdr(cdr(car(car(d)))); }
  public static Datum cddadr(Datum d) throws Exception { return cdr(cdr(car(cdr(d)))); }
  public static Datum cdddar(Datum d) throws Exception { return cdr(cdr(cdr(car(d)))); }
  public static Datum cddddr(Datum d) throws Exception { return cdr(cdr(cdr(cdr(d)))); }

  public static void setCar(Datum d, Datum newVal) throws Exception {
    if(!(d instanceof Type.Pair))
      throw new Exceptionf("Invalid 'set-car! in evaluator: %s isn't a pair!", d.write());
    ((Type.Pair)d).car = newVal;
  }

  public static void setCdr(Datum d, Datum newVal) throws Exception {
    if(!(d instanceof Type.Pair))
      throw new Exceptionf("Invalid 'set-cdr! in evaluator: %s isn't a pair!", d.write());
    ((Type.Pair)d).cdr = newVal;
  }

  public static void setCaar(Datum d, Datum newVal) throws Exception { setCar(car(d),newVal); }
  public static void setCadr(Datum d, Datum newVal) throws Exception { setCar(cdr(d),newVal); }
  public static void setCdar(Datum d, Datum newVal) throws Exception { setCdr(car(d),newVal); }
  public static void setCddr(Datum d, Datum newVal) throws Exception { setCdr(cdr(d),newVal); }

  public static void setCaaar(Datum d, Datum newVal) throws Exception { setCar(car(car(d)),newVal); }
  public static void setCaadr(Datum d, Datum newVal) throws Exception { setCar(car(cdr(d)),newVal); }
  public static void setCadar(Datum d, Datum newVal) throws Exception { setCar(cdr(car(d)),newVal); }
  public static void setCaddr(Datum d, Datum newVal) throws Exception { setCar(cdr(cdr(d)),newVal); }
  public static void setCdaar(Datum d, Datum newVal) throws Exception { setCdr(car(car(d)),newVal); }
  public static void setCdadr(Datum d, Datum newVal) throws Exception { setCdr(car(cdr(d)),newVal); }
  public static void setCddar(Datum d, Datum newVal) throws Exception { setCdr(cdr(car(d)),newVal); }
  public static void setCdddr(Datum d, Datum newVal) throws Exception { setCdr(cdr(cdr(d)),newVal); }

  public static void setCaaaar(Datum d, Datum newVal) throws Exception { setCar(car(car(car(d))),newVal); }
  public static void setCaaadr(Datum d, Datum newVal) throws Exception { setCar(car(car(cdr(d))),newVal); }
  public static void setCaadar(Datum d, Datum newVal) throws Exception { setCar(car(cdr(car(d))),newVal); }
  public static void setCaaddr(Datum d, Datum newVal) throws Exception { setCar(car(cdr(cdr(d))),newVal); }
  public static void setCadaar(Datum d, Datum newVal) throws Exception { setCar(cdr(car(car(d))),newVal); }
  public static void setCadadr(Datum d, Datum newVal) throws Exception { setCar(cdr(car(cdr(d))),newVal); }
  public static void setCaddar(Datum d, Datum newVal) throws Exception { setCar(cdr(cdr(car(d))),newVal); }
  public static void setCadddr(Datum d, Datum newVal) throws Exception { setCar(cdr(cdr(cdr(d))),newVal); }
  public static void setCdaaar(Datum d, Datum newVal) throws Exception { setCdr(car(car(car(d))),newVal); }
  public static void setCdaadr(Datum d, Datum newVal) throws Exception { setCdr(car(car(cdr(d))),newVal); }
  public static void setCdadar(Datum d, Datum newVal) throws Exception { setCdr(car(cdr(car(d))),newVal); }
  public static void setCdaddr(Datum d, Datum newVal) throws Exception { setCdr(car(cdr(cdr(d))),newVal); }
  public static void setCddaar(Datum d, Datum newVal) throws Exception { setCdr(cdr(car(car(d))),newVal); }
  public static void setCddadr(Datum d, Datum newVal) throws Exception { setCdr(cdr(car(cdr(d))),newVal); }
  public static void setCdddar(Datum d, Datum newVal) throws Exception { setCdr(cdr(cdr(car(d))),newVal); }
  public static void setCddddr(Datum d, Datum newVal) throws Exception { setCdr(cdr(cdr(cdr(d))),newVal); }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <define-macro>
  private static void validateDefineMacro(Datum d) throws Exception {
    if(cadr(d) instanceof Type.Pair) {
      if(!(caadr(d) instanceof Type.Symbol))
        throw new Exceptionf("Invalid 'define-macro Syntax (non-symbol macro name): %s", d.write());
      if(!(cadadr(d) instanceof Type.Symbol))
        throw new Exceptionf("Invalid 'define-macro Syntax (non-symbol macro arg): %s", d.write());
      if(!(cddadr(d) instanceof Type.Nil))
        throw new Exceptionf("Invalid 'define-macro Syntax (too many args: only accepts 1): %s", d.write());
      if(cddr(d) instanceof Type.Nil)
        throw new Exceptionf("Invalid 'define-macro Syntax (missing macro body): %s", d.write());
    } else {
      throw new Exceptionf("Invalid 'define-macro Syntax (no macro-name & macro-argument list): %s", d.write());
    }
  }


  private static Datum evalDefineMacro(Environment env, Datum d) throws Exception {
    validateDefineMacro(d);
    String macroName = ((Type.Symbol)caadr(d)).value;
    String macroArg = ((Type.Symbol)cadadr(d)).value;
    Datum body = cddr(d);
    if(!(cdr(body) instanceof Type.Nil)) {
      body = new Type.Pair(new Type.Symbol("begin"),body);
    } else {
      body = car(body);
    }
    env.define(macroName,new Type.Macro(macroArg,body,env));
    return new Type.Void();
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <define>
  private static void validateDefine(Datum d) throws Exception {
    if(cadr(d) instanceof Type.Symbol) {
      if(!(cdddr(d) instanceof Type.Nil))
        throw new Exceptionf("Invalid 'define Syntax (too many variable values): %s", d.write());
    } else if(cadr(d) instanceof Type.Pair) {
      if(!(caadr(d) instanceof Type.Symbol))
        throw new Exceptionf("Invalid 'define Syntax (non-symbol function name): %s", d.write());
      if(cddr(d) instanceof Type.Nil)
        throw new Exceptionf("Invalid 'define Syntax (missing function body): %s", d.write());
    } else {
      throw new Exceptionf("Invalid 'define Syntax (can't define a literal to a another value!): %s", d.write());
    }
  }


  private static Datum evalDefine(Environment env, Datum d) throws Exception {
    validateDefine(d);
    // Expand (define (f a ...) b ...) to be (define f (lambda (a ...) b ...))
    if(cadr(d) instanceof Type.Pair) {
      Datum lambdaExpression = d.copy();
      Datum variable = caadr(d);
      setCar(lambdaExpression, new Type.Symbol("lambda"));
      setCadr(lambdaExpression,cdadr(lambdaExpression));
      Datum params = cadr(lambdaExpression);
      if(params instanceof Type.Symbol) // account for unary-variadic procedure expansions
        setCadr(lambdaExpression,Type.Pair.List(new Type.Symbol("."),params));
      Datum newExpression = Type.Pair.List(new Type.Symbol("define"), variable, lambdaExpression);
      return eval(env,newExpression);
    }
    String variable = ((Type.Symbol)cadr(d)).value;
    Datum value = eval(env,caddr(d));
    env.define(variable,value);
    return new Type.Void();
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <set!>
  private static void validateSet(Datum d) throws Exception {
    if(!(cadr(d) instanceof Type.Symbol))
      throw new Exceptionf("Invalid 'set! Syntax (can only set variables!): %s", d.write());
    if(!(cdddr(d) instanceof Type.Nil))
      throw new Exceptionf("Invalid 'set! Syntax (only accepts 2 arguments): %s", d.write());
  }

  
  private static Datum evalSet(Environment env, Datum d) throws Exception {
    validateSet(d);
    String variable = ((Type.Symbol)cadr(d)).value;
    Datum value = eval(env,caddr(d));
    env.set(variable,value);
    return new Type.Void();
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <if>
  private static void validateIf(Datum d) throws Exception {
    if(!(cdddr(d) instanceof Type.Nil) && !(cddddr(d) instanceof Type.Nil))
      throw new Exceptionf("Invalid 'if Syntax (only accepts 2 or 3 arguments): %s", d.write());
  }

  
  private static Datum evalIf(Environment env, Datum d) throws Exception {
    validateIf(d);
    if(eval(env,cadr(d)).isTruthy())
      return eval(env,caddr(d));
    Datum alternativeExpression = cdddr(d);
    if(alternativeExpression instanceof Type.Nil)
      return new Type.Void();
    return eval(env,car(alternativeExpression));
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <lambda>
  private static String validParameters(Datum d) throws Exception { // returns the reason for invalid params
    Datum params = cadr(d);
    if(params instanceof Type.Nil) return null;
    if(!(params instanceof Type.Pair)) return "non-nil & non-pair parameters given!";
    Datum iterator = params;
    while(iterator instanceof Type.Pair) {
      Datum param = car(iterator);
      if(!(param instanceof Type.Symbol)) return String.format("non-symbol parameter %s found!", param.write());
      iterator = cdr(iterator);
    }
    if(iterator instanceof Type.Nil || iterator instanceof Type.Symbol) return null;
    return String.format("non-symbol parameter %s found!", iterator.write());
  }


  private static void validateLambda(Datum d) throws Exception {
    String paramsErrMsg = validParameters(d);
    if(paramsErrMsg != null)
      throw new Exceptionf("Invalid 'lambda Syntax: invalid parameters: %s", paramsErrMsg);
    if(cddr(d) instanceof Type.Nil)
      throw new Exception("Invalid 'lambda Syntax: missing a function body!");
  }

  // Returns whether params are variadic & populates <parameters>
  private static boolean extractParameters(Datum d, ArrayList<String> parameters) throws Exception {
    Datum params = cadr(d);
    if(params instanceof Type.Nil) return false;
    if(isTaggedList((Type.Pair)params,".")) {
      parameters.add(((Type.Symbol)cadr(params)).value);
      return true;
    }
    while(params instanceof Type.Pair) {
      parameters.add(((Type.Symbol)car(params)).value);
      params = cdr(params);
    }
    if(params instanceof Type.Nil) return false;
    parameters.add(((Type.Symbol)params).value);
    return true;
  }

  
  private static Datum evalLambda(Environment env, Datum d) throws Exception {
    validateLambda(d);
    ArrayList<String> parameters = new ArrayList<String>();
    boolean isVariadic = extractParameters(d,parameters);
    Datum body = cddr(d);
    if(!(cdr(body) instanceof Type.Nil)) {
      body = new Type.Pair(new Type.Symbol("begin"),body);
    } else {
      body = car(body);
    }
    return new Type.CompoundProcedure(parameters,body,env,isVariadic);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <begin>
  private static Datum evalBegin(Environment env, Datum d) throws Exception {
    Datum iterator = cdr(d);
    Datum result = new Type.Void();
    while(iterator instanceof Type.Pair) {
      result = eval(env,car(iterator));
      iterator = cdr(iterator);
    }
    return result;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing <quote>
  private static void validateQuote(Datum d) throws Exception {
    if(!(cddr(d) instanceof Type.Nil)) 
      throw new Exceptionf("Invalid 'quote Syntax (accepts exactly 1 argument): %s", d.write());
  }


  private static Datum quote(Datum d) {
    return Type.Pair.List(new Type.Symbol("quote"),d);
  }


  private static Datum generateQuotedList(Environment env, Datum d) throws Exception {
    if(!(d instanceof Type.Pair)) return eval(env,quote(d));
    return new Type.Pair(eval(env,quote(car(d))),generateQuotedList(env,cdr(d)));
  }


  private static Datum evalQuote(Environment env, Datum d) throws Exception {
    validateQuote(d);
    Datum quoted = cadr(d);
    if(!(quoted instanceof Type.Pair)) return quoted;
    return generateQuotedList(env,quoted);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing variables
  private static Datum evalSymbol(Environment env, Type.Symbol s) throws Exception {
    return env.get(s.value);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Representing procedural & macro application
  private static Datum extractCallable(Environment env, Datum d) throws Exception {
    Datum callableExpression = car(d);
    Datum callableValue = eval(env,callableExpression);
    if(!(callableValue instanceof Type.Procedure) && !(callableValue instanceof Type.Macro))
      throw new Exceptionf("Can't apply non-procedure & non-macro %s in %s!", callableValue.write(), callableExpression.write());
    return callableValue;
  }


  private static ArrayList<Datum> extractArguments(Environment env, Datum d) throws Exception {
    ArrayList<Datum> arguments = new ArrayList<Datum>();
    Datum argIterator = cdr(d);
    while(argIterator instanceof Type.Pair) {
      arguments.add(eval(env,car(argIterator)));
      argIterator = cdr(argIterator);
    }
    return arguments;
  }


  private static Datum evalApplication(Environment env, Datum d) throws Exception {
    Datum callable = extractCallable(env,d);
    // Apply a procedure
    if(callable instanceof Type.Procedure) {
      return ((Type.Procedure)callable).callWith(env,extractArguments(env,d));
    // Apply a macro
    } else {
      return ((Type.Macro)callable).callWith(env,d);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // Implementing the Core Evaluation Loop (tree-walking!)
  // => NOTE: SHOULD NEVER MUTATE <d>!
  public static Datum eval(Environment env, Datum d) throws Exception {
    if(d instanceof Type.Symbol) return evalSymbol(env,(Type.Symbol)d);
    if(!(d instanceof Type.Pair)) return d;
    Type.Pair expr = (Type.Pair)d;
    if(isTaggedList(expr,"define-macro")) return evalDefineMacro(env,d);
    if(isTaggedList(expr,"define") || isTaggedList(expr,"def")) return evalDefine(env,d);
    if(isTaggedList(expr,"set!")) return evalSet(env,d);
    if(isTaggedList(expr,"if")) return evalIf(env,d);
    if(isTaggedList(expr,"lambda")) return evalLambda(env,d);
    if(isTaggedList(expr,"begin")) return evalBegin(env,d);
    if(isTaggedList(expr,"quote")) return evalQuote(env,d);
    return evalApplication(env,d);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Read an expression from stdin (returns <null> if reads EOF)
  public static Datum read(BufferedReader br) throws Exception {
    StringBuilder sb = new StringBuilder();
    while(true) {
      try {
        String input = br.readLine();
        if(input == null) return null; // EOF detected
        if(input.length() == 0) continue;
        sb.append(input);
        Pair<Datum,Integer> result = Reader.read(sb.toString());
        Primitive.IO.lastPrintedANewline = true; // from the newline input by the user's <enter>/<return> key stroke
        return result.first;
      } catch(Reader.IncompleteException e) {
        continue;
      }
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // Implementing our REPL
  private static void printReplIntro() {
    System.out.println("Scheme160 Version 1.0\nEnter '(exit)' to Exit");
  }


  private static void printReplPrompt() {
    if(!Primitive.IO.lastPrintedANewline) System.out.println("");
    System.out.print("> ");
    Primitive.IO.lastPrintedANewline = false;
  }


  private static Datum readFullExpression(BufferedReader br) {
    while(true) {
      try {
        printReplPrompt();
        Datum readDatum = read(br);
        // Account for EOF => triggers REPL termination!
        if(readDatum == null) {
          System.out.println('\n'+Primitive.Sys.EXIT_MESSAGE);
          System.exit(0);
        }
        return readDatum;
      } catch(Exception e) {
        System.err.printf("\n%s\n",e.getMessage());
      }
    }
  }


  private static Datum evalFullExpression(BufferedReader br) {
    while(true) {
      try {
        return eval(Runtime.globalEnvironment,readFullExpression(br));
      } catch(Exception e) {
        System.err.printf("\nSCM160 ERROR: %s\n", e.getMessage());
        Runtime.CallStack.printTrace();
        Runtime.CallStack.reset();
        System.err.println("");
      }
    }
  }


  private static void launchRepl() throws Exception {
    Runtime.inREPL = true; // trigger exit message to be printed
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    printReplIntro();
    while(true) {
      Datum result = evalFullExpression(br);
      if(!(result instanceof Type.Void)) System.out.printf("%s\n", result.write());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // Implementing our File Interpreter
  private static void launchScript(String[] args) throws Exception {
    boolean loadingIntoREPL = args[0].equals("-l");
    int filenameIndex = loadingIntoREPL ? 1 : 0;
    if(filenameIndex >= args.length) {
      System.err.println("ERROR: No filename given to load into the REPL!");
      return;
    }
    // Populate *argv*
    String filename = args[filenameIndex];
    for(int i = args.length-1; i > filenameIndex; --i)
      Primitive.Sys.argv = new Type.Pair(new Type.String(args[i]),Primitive.Sys.argv);
    // Initialize the runtime & load the file
    Runtime.initialize();
    Primitive.Sys.Load.loadFileInEnvironment(Runtime.globalEnvironment,filename);
    // Launch REPL if given the "-l" flag prior the filename
    if(loadingIntoREPL) launchRepl();
  }


  ////////////////////////////////////////////////////////////////////////////
  // Implementing our Interpreter
  public static void launchScheme160Session(String[] args) {
    try {
      if(args.length == 0) {
        Runtime.initialize();
        launchRepl();
      } else {
        launchScript(args);
      }
    } catch(Exception e) {
      System.err.printf("Driver Loop Caught Error %s\n", e);
      Runtime.CallStack.printTrace();
      e.printStackTrace();
    }
  }
}
