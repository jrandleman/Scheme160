// Author: Jordan Randleman - Type.Nil
// Purpose:
//    Nil primitive type, also known as the "empty list".

package Type;
import java.util.Objects;

public class Nil extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "nil";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Nil;
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
    return "()";
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