// Author: Jordan Randleman - Type.String
// Purpose:
//    String primitive type.

package Type;
import java.util.Objects;

public class String extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Value Field
  public java.lang.String value = "";


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public String(java.lang.String s) {
    value = s;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "string";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof String && ((String)o).value.equals(value);
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
    return value;
  }

  public java.lang.String write() {
    return '"' + Util.StringParser.escape(value) + '"';
  }


  ////////////////////////////////////////////////////////////////////////////
  // Copying semantics
  public Datum copy() {
    return this;
  }
}