// Author: Jordan Randleman - Type.Symbol
// Purpose:
//    Symbol primitive type, used extensively in metaprogramming.

package Type;
import java.util.Objects;

public class Symbol extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Value Field
  public java.lang.String value = "";


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public Symbol(java.lang.String s) {
    value = s;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "symbol";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Symbol && ((Symbol)o).value.equals(value);
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
    return display();
  }


  ////////////////////////////////////////////////////////////////////////////
  // Copying
  public Datum copy() {
    return this;
  }
}