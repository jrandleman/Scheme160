// Author: Jordan Randleman - Type.Macro
// Purpose:
//    Macro primitive type. Wraps a procedure with special invocation semantics.

package Type;
import java.util.ArrayList;
import java.util.Objects;

public class Macro extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Static Default Macro Name
  public static final java.lang.String DEFAULT_NAME = "#!ANONYMOUS";


  ////////////////////////////////////////////////////////////////////////////
  // Internal Procedure Field
  private CompoundProcedure innerProcedure;


  ////////////////////////////////////////////////////////////////////////////
  // Name Field
  public java.lang.String name = DEFAULT_NAME;


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public Macro(java.lang.String argName, Datum body, Environment definitionEnv) {
    ArrayList<java.lang.String> args = new ArrayList<java.lang.String>();
    args.add(argName);
    innerProcedure = new CompoundProcedure(args,body,definitionEnv,false);
    bindName(DEFAULT_NAME);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Name binding (used by Environment.java)
  public void bindName(java.lang.String name) {
    this.name = name;
    innerProcedure.name = java.lang.String.format("#<macro-procedure %s>",name);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Invoke the Macro via <callWith>
  public Datum callWith(Environment currentEnv, Datum expr) throws Exception {
    ArrayList<Datum> args = new ArrayList<Datum>();
    args.add(expr);
    return Util.Core.eval(currentEnv,innerProcedure.callWith(currentEnv,args));
  }


  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "macro";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Macro && (Macro)o == this;
  }

  public boolean equals(Object o) {
    return eq(o);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Hash code
  public int hashCode() {
    return Objects.hash(this);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Serialization
  public java.lang.String display() {
    return java.lang.String.format("#<macro %s>", name);
  }

  public java.lang.String write() {
    return display();
  }


  ////////////////////////////////////////////////////////////////////////////
  // Copying
  public Datum copy() {
    return this;
  }
}