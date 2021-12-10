// Author: Jordan Randleman - Primitive.Functional
// Purpose:
//    Java primitives for functional programming support.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class Functional {
  ////////////////////////////////////////////////////////////////////////////
  // compose
  public static class Compose implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) 
        throw new Exceptionf("'compose expects at least 1 procedure arg: %s", Exceptionf.profileArgs(parameters));
      // Generate function parameters
      ArrayList<String> params = new ArrayList<String>();
      params.add("args");
      // Generate function body
      Datum rootProcedure = parameters.get(parameters.size()-1);
      if(!(rootProcedure instanceof Type.Procedure)) 
        throw new Exceptionf("'compose received a non-procedure arg %s!", rootProcedure.profile());
      Datum body = Type.Pair.List(new Type.Symbol("apply"),rootProcedure,new Type.Symbol("args"));
      for(int i = parameters.size()-2; i >= 0; --i) {
        Datum procedure = parameters.get(i);
        if(!(procedure instanceof Type.Procedure)) 
          throw new Exceptionf("'compose received a non-procedure arg %s!", procedure.profile());
        body = Type.Pair.List(procedure,body);
      }
      return new Type.CompoundProcedure(params,body,Util.Runtime.globalEnvironment,true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // bind
  public static class Bind implements Type.Primitive {
    private static Datum convertArgsToList(ArrayList<Datum> parameters) {
      Datum argsList = new Type.Nil();
      for(int i = parameters.size()-1; i >= 1; --i)
        argsList = new Type.Pair(parameters.get(i),argsList);
      return new Type.Pair(new Type.Symbol("list"),argsList);
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) 
        throw new Exceptionf("'bind expects at least 1 procedure and optional args: %s", Exceptionf.profileArgs(parameters));
      ArrayList<String> params = new ArrayList<String>();
      params.add("args");
      Datum argsList = convertArgsToList(parameters);
      Datum body = Type.Pair.List(new Type.Symbol("apply"),parameters.get(0),Type.Pair.List(new Type.Symbol("append"),argsList,new Type.Symbol("args")));
      return new Type.CompoundProcedure(params,body,Util.Runtime.globalEnvironment,true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // id
  public static class Id implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'id expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return parameters.get(0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // procedure?
  public static class IsProcedure implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'procedure? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Procedure);
    }
  }
}