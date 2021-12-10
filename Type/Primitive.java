// Author: Jordan Randleman - Type.Primitive
// Purpose:
//    Primitive interface that all Java primitives must implement to be used as Scheme procedures.

package Type;
import java.util.ArrayList;

public interface Primitive {
  public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception;
}