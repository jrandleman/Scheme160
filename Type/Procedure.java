// Author: Jordan Randleman - Type.Procedure
// Purpose:
//    Abstract Base Class for Scheme procedures -- the contract all Scheme procedure
//    types must implement to be used applied by the core interpreter. Note that
//    Scheme programmers will only ever see one "procedure" type in the runtime, and
//    that this base class primarily serves to support multiple internal procedure types,
//    the differences between which are abstracted away from the Scheme programmer.
//
//    For example, Java primitive procedures (defined by the interpreter's implementation) 
//    and Scheme compound procedures (defined by Scheme programmers) must be applied 
//    differently under the hood, but to the Scheme programmer, they are indistinguishable.

package Type;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Procedure extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Static Default Function Name (for anonymous lambdas)
  public static final java.lang.String DEFAULT_NAME = "#!ANONYMOUS";


  ////////////////////////////////////////////////////////////////////////////
  // Name field
  public java.lang.String name = DEFAULT_NAME;


  ////////////////////////////////////////////////////////////////////////////
  // Name binding (used by Environment.java)
  public void bindName(java.lang.String name) {
    this.name = name;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Application Abstraction
  public abstract Datum callWith(Environment currentEnv, ArrayList<Datum> arguments) throws Exception;


  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "procedure";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Procedure && (Procedure)o == this;
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
    return java.lang.String.format("#<procedure %s>", name);
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