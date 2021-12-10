// Author: Jordan Randleman - Primitive.TypePredicate
// Purpose:
//    Java primitives for type predicates.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class TypePredicate {
  ////////////////////////////////////////////////////////////////////////////
  // typeof
  public static class Typeof implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'typeof expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Symbol(parameters.get(0).type());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // void?
  public static class IsVoid implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'void? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Void);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // boolean?
  public static class IsBoolean implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'boolean? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Boolean);
    }
  }
}