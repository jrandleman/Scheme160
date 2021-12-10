// Author: Jordan Randleman - Type.Pair
// Purpose:
//    Pair primitive type.

package Type;
import java.util.Objects;

public class Pair extends Datum {
  ////////////////////////////////////////////////////////////////////////////
  // Car/Cdr Fields
  public Datum car;
  public Datum cdr;


  ////////////////////////////////////////////////////////////////////////////
  // Constructor
  public Pair(Datum car, Datum cdr) {
    this.car = car;
    this.cdr = cdr;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Static List Generator
  public static Datum List(Datum ... listContents) {
    Datum d = new Nil();
    for(int i = listContents.length-1; i >= 0; --i)
      d = new Pair(listContents[i],d);
    return d;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Static List Predicate
  public static boolean isList(Datum d) {
    if(d instanceof Nil) return true;
    if(!(d instanceof Pair)) return false;
    Datum iterator = d;
    while(iterator instanceof Pair) iterator = ((Pair)iterator).cdr;
    return iterator instanceof Nil;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Type
  public java.lang.String type() {
    return "pair";
  }


  ////////////////////////////////////////////////////////////////////////////
  // Truthiness
  public boolean isTruthy() {
    return true;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Equality
  public boolean eq(Object o) {
    return o instanceof Pair && ((Pair)o) == this;
  }

  public boolean equals(Object o) {
    return o instanceof Pair && ((Pair)o).car.equals(car) && ((Pair)o).cdr.equals(cdr);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Hash code
  public int hashCode() {
    return Objects.hash(type(),car,cdr);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Serialization
  public java.lang.String display() {
    if(!(cdr instanceof Pair) && !(cdr instanceof Nil)) { // printing non-list pair
      return "(" + car.display() + " . " + cdr.display() + ")";
    } else { // printing a list
      Datum iterator = this;
      boolean addSpace = false;
      StringBuilder list = new StringBuilder("(");
      while(iterator instanceof Pair) {
        if(addSpace) {
          list.append(' ');
        } else {
          addSpace = true;
        }
        list.append(((Pair)iterator).car.display());
        iterator = ((Pair)iterator).cdr;
      }
      if(!(iterator instanceof Nil)) { // dotted list
        list.append(" . ");
        list.append(iterator.display());
      }
      list.append(')');
      return list.toString();
    }
  }

  public java.lang.String write() {
    if(!(cdr instanceof Pair) && !(cdr instanceof Nil)) { // printing non-list pair
      return "(" + car.write() + " . " + cdr.write() + ")";
    } else { // printing a list
      Datum iterator = this;
      boolean addSpace = false;
      StringBuilder list = new StringBuilder("(");
      while(iterator instanceof Pair) {
        if(addSpace) {
          list.append(' ');
        } else {
          addSpace = true;
        }
        list.append(((Pair)iterator).car.write());
        iterator = ((Pair)iterator).cdr;
      }
      if(!(iterator instanceof Nil)) { // dotted list
        list.append(" . ");
        list.append(iterator.write());
      }
      list.append(')');
      return list.toString();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // Copying
  public Datum copy() {
    return new Pair(car.copy(),cdr.copy());
  }
}