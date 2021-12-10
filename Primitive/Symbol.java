// Author: Jordan Randleman - Primitive.Symbol
// Purpose:
//    Java primitives for symbol procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class Symbol {
  ////////////////////////////////////////////////////////////////////////////
  // symbol-append
  public static class SymbolAppend implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 1) 
        throw new Exceptionf("'symbol-append requires at least 1 symbol arg: %s", Exceptionf.profileArgs(parameters));
      StringBuilder sb = new StringBuilder();
      for(Datum p : parameters) {
        if(!(p instanceof Type.Symbol))
          throw new Exceptionf("'symbol-append received a non-symbol object %s!", p.profile());
        sb.append(((Type.Symbol)p).value);
      }
      return new Type.Symbol(sb.toString());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // symbol?
  public static class IsSymbol implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'symbol? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Symbol);
    }
  }
}