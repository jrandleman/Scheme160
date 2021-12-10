// Author: Jordan Randleman - Primitive.Utility
// Purpose:
//    Java primitives for utility procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class Utility {
  ////////////////////////////////////////////////////////////////////////////
  // not
  public static class Not implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'not didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(!parameters.get(0).isTruthy());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // force
  public static class Force implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Procedure)) 
        throw new Exceptionf("'force didn't receive exactly 1 procedure: %s", Exceptionf.profileArgs(parameters));
      return ((Type.Procedure)parameters.get(0)).callWith(Util.Runtime.globalEnvironment,new ArrayList<Datum>());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // apply
  public static class Apply implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2) 
        throw new Exceptionf("'apply didn't receive exactly 2 args (procedure & arg-list): %s", Exceptionf.profileArgs(parameters));
      Datum procedure = parameters.get(0);
      Datum arguments = parameters.get(1);
      if(!(procedure instanceof Type.Procedure)) 
        throw new Exceptionf("'apply 1st arg %s isn't a procedure!", procedure.profile());
      if(!Type.Pair.isList(arguments)) 
        throw new Exceptionf("'apply 2nd arg %s isn't an arg list!", arguments.profile());
      ArrayList<Datum> args = new ArrayList<Datum>();
      while(arguments instanceof Type.Pair) {
        Type.Pair argumentsPair = (Type.Pair)arguments;
        args.add(argumentsPair.car);
        arguments = argumentsPair.cdr;
      }
      return ((Type.Procedure)procedure).callWith(Util.Runtime.globalEnvironment,args);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // eval
  public static class Eval implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'eval didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return Util.Core.eval(currentEnv,parameters.get(0));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // copy
  public static class Copy implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'copy didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return parameters.get(0).copy();
    }
  }
}