// Author: Jordan Randleman - Type.PrimitiveProcedure
// Purpose:
//    Java primitive procedure specialization of "Type.Procedure".
//    Wraps a "Type.Primitive" object under the hood.

package Type;
import java.util.ArrayList;
import java.util.Objects;

public class PrimitiveProcedure extends Procedure {
  ////////////////////////////////////////////////////////////////////////////
  // Internal primitive procedure field
  private Primitive prm;


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public PrimitiveProcedure(java.lang.String name, Primitive prm) {
    this.name = name;
    this.prm = prm;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Application Abstraction
  public Datum callWith(Environment currentEnv, ArrayList<Datum> arguments) throws Exception {
    Util.Runtime.CallStack.push(name);
    Datum result = prm.callWith(currentEnv,arguments);
    Util.Runtime.CallStack.pop();
    return result;
  }
}