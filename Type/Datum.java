// Author: Jordan Randleman - Type.Datum
// Purpose:
//    Abstract Base Class for Scheme objects -- a contract all Scheme objects 
//    types must implement to be used by the core interpreter. This bakes in 
//    extensibility for our interpreter, adding in a new primitive type only
//    requires the extension of this contract!

package Type;

public abstract class Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Type
  public abstract java.lang.String type();


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public abstract boolean isTruthy();


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public abstract boolean eq(Object o);     // shallow
  public abstract boolean equals(Object o); // deep (recursive for containers)


  ////////////////////////////////////////////////////////////////////////////
  // Hash code
  public abstract int hashCode();


  ////////////////////////////////////////////////////////////////////////////
  // Serialization
  public abstract java.lang.String display(); // human-readable
  public abstract java.lang.String write();   // machine-readable

  // Define <toString> as an alias of <write>
  public java.lang.String toString() {
    return write();
  }

  // Profiler to help print datum details in error messages
  public java.lang.String profile() {
    return java.lang.String.format("%s of type \"%s\"", write(), type());
  }


  ////////////////////////////////////////////////////////////////////////////
  // Copying
  public abstract Datum copy();
}