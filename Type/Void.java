// Author: Jordan Randleman - Type.Void
// Purpose:
//    Void primitive type, the "value" returned by special forms like "define" and "set!".

package Type;
import java.util.Objects;

public class Void extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "void";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Void;
  }

  public boolean equals(Object o) {
    return eq(o);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Hash code
  public int hashCode() {
    return Objects.hash(type());
  }


  ////////////////////////////////////////////////////////////////////////////
  // Serialization
  public java.lang.String display() {
    return "";
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