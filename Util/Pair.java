// Author: Jordan Randleman - Util.Pair
// Purpose:
//    Simple generic pair data structure. Used extensively by the reader.

package Util;

public class Pair<T,U> {
  public T first;
  public U second;
  public Pair(T t, U u) {
    first = t;
    second = u;
  }
}