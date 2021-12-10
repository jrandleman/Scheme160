// Author: Jordan Randleman - Primitive.Equality
// Purpose:
//    Java primitives for equality procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class Equality {
  ////////////////////////////////////////////////////////////////////////////
  // eq?
  public static class IsEq implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      int n = parameters.size();
      if(n == 0) throw new Exceptionf("'eq? expects at least 1 argument: %s", Exceptionf.profileArgs(parameters));
      for(int i = 0; i < n-1; ++i)
        if(!parameters.get(i).eq(parameters.get(i+1)))
          return new Type.Boolean(false);
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // equal?
  public static class IsEqual implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      int n = parameters.size();
      if(n == 0) throw new Exceptionf("'equal? expects at least 1 argument: %s", Exceptionf.profileArgs(parameters));
      for(int i = 0; i < n-1; ++i)
        if(!parameters.get(i).equals(parameters.get(i+1)))
          return new Type.Boolean(false);
      return new Type.Boolean(true);
    }
  }
}