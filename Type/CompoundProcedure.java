// Author: Jordan Randleman - Type.CompoundProcedure
// Purpose:
//    Scheme compound procedure specialization of "Type.Procedure".
//    Compound procedures have 3 main components:
//      0) Definition Environment
//         => This is critical since all Scheme compound procedures are closures,
//            we need a pointer to the enclosing environment in order to dynamically
//            find captured variables.
//      1) Parameter List
//         => Critical for obvious reasons, to extend such as defined variables upon 
//            each invocation of the procedure. Note that as an optimization technique
//            we parse such preemptively to also keep track of whether the parameters 
//            are variadic or not.
//      2) Body
//         => A Scheme expression to be evaluated upon application of the procedure in a 
//            new environment extended with the parameters defined as variables with the 
//            values of the application arguments, whose parent environment is the 
//            procedure's definition environment.
//
//    Note that procedures have their name bound not upon construction (all compound Scheme
//    procedures are anonymous upon creation), but rather by the environment's binding 
//    methods during "define" and "set!" invocations.

package Type;
import Util.Exceptionf;
import java.util.ArrayList;
import java.util.Objects;

public class CompoundProcedure extends Procedure {
  ////////////////////////////////////////////////////////////////////////////
  // Internal compound procedure fields
  private Environment definitionEnv;
  private ArrayList<java.lang.String> parameters;
  private Datum body;
  private boolean isVariadic;


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public CompoundProcedure(ArrayList<java.lang.String> parameters, Datum body, Environment definitionEnv, boolean isVariadic) {
    this.name = DEFAULT_NAME;
    this.parameters = parameters;
    this.body = body;
    this.definitionEnv = definitionEnv;
    this.isVariadic = isVariadic;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Application Abstraction
  private void validateEnvironmentExtension(ArrayList<Datum> arguments) throws Exception {
    if(arguments.size() < parameters.size() && !isVariadic) {
      throw new Exceptionf("Procedure %s wasn't given enough arguments!", name);
    } else if(arguments.size() < parameters.size()-1 && isVariadic) {
      throw new Exceptionf("Procedure %s wasn't given enough arguments!", name);
    } else if(arguments.size() > parameters.size() && !isVariadic) {
      throw new Exceptionf("Procedure %s was given too many arguments!", name);
    } 
  }


  private Datum createVariadicParameterList(ArrayList<Datum> arguments, int i, int totalArguments) {
    if(i == totalArguments) return new Type.Nil();
    return new Type.Pair(arguments.get(i), createVariadicParameterList(arguments,i+1,totalArguments));
  }


  private Environment getExtendedEnvironment(ArrayList<Datum> arguments) throws Exception {
    validateEnvironmentExtension(arguments);
    Environment newEnv = new Environment(definitionEnv);
    int n = parameters.size();
    for(int i = 0, offset = isVariadic ? 1 : 0; i < n-offset; ++i)
      newEnv.define(parameters.get(i),arguments.get(i));
    if(isVariadic)
      newEnv.define(parameters.get(n-1),createVariadicParameterList(arguments,n-1,arguments.size()));
    return newEnv;
  }


  public Datum callWith(Environment currentEnv, ArrayList<Datum> arguments) throws Exception {
    Util.Runtime.CallStack.push(name);
    Datum result = Util.Core.eval(getExtendedEnvironment(arguments),body);
    Util.Runtime.CallStack.pop();
    return result;
  }
}