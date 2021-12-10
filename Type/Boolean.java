// Author: Jordan Randleman - Type.Boolean
// Purpose:
//    Boolean primitive type.

package Type;
import java.util.Objects;

public class Boolean extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Value Field
  public boolean value = false;


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public Boolean(boolean b) {
    value = b;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "boolean";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return value;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Boolean && ((Boolean)o).value == value;
  }

  public boolean equals(Object o) {
    return eq(o);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Hash code
  public int hashCode() {
    return Objects.hash(type(),value);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Serialization
  public java.lang.String display() {
    if(value) return "#t";
    return "#f";
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