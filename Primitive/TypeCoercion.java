// Author: Jordan Randleman - Primitive.TypeCoercion
// Purpose:
//    Java primitives for type coercion procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class TypeCoercion {
  ////////////////////////////////////////////////////////////////////////////
  // string->number
  public static class StringToNumber implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string->number expects exactly 1 string arg: %s", Exceptionf.profileArgs(parameters));
      try {
        return new Type.Number(Double.parseDouble(((Type.String)parameters.get(0)).value));
      } catch(Exception e) {
        return new Type.Boolean(false);
      }
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // number->string
  public static class NumberToString implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Number)) 
        throw new Exceptionf("'number->string expects exactly 1 number arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(String.valueOf(((Type.Number)parameters.get(0)).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // write-to-string
  public static class WriteToString implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'write-to-string expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(parameters.get(0).write());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // display-to-string
  public static class DisplayToString implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'display-to-string expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(parameters.get(0).display());
    }
  }
}