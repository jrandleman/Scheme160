// Author: Jordan Randleman - Type.Environment
// Purpose:
//    Scheme environment type used internally by the evaluator and Scheme procedures to
//    represent variable bindings in a particular scope. Nested scopes are supported by 
//    having each environment contain a pointer to its enclosing environment (the global 
//    environment's "super" [enclosing] environment is <null>).

package Type;
import Util.Exceptionf;
import java.util.HashMap;

public class Environment {
  ////////////////////////////////////////////////////////////////////////////
  // Fields
  private Environment superEnv;
  private HashMap<java.lang.String,Datum> bindings;


  ////////////////////////////////////////////////////////////////////////////
  // Constructors
  public Environment(){
    bindings = new HashMap<java.lang.String,Datum>();
  }
  public Environment(Environment superEnv){
    bindings = new HashMap<java.lang.String,Datum>();
    this.superEnv = superEnv;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Get value
  public Datum get(java.lang.String name) throws Exception {
    Datum result = bindings.get(name);
    if(result == null) {
      if(superEnv == null) throw new Exceptionf("Variable %s doesn't exist!", name);
      return superEnv.get(name);
    }
    return result;
  }


  ////////////////////////////////////////////////////////////////////////////
  // Set value
  public void set(java.lang.String name, Datum newValue) throws Exception {
    Datum result = bindings.get(name);
    if(result == null) {
      if(superEnv == null) throw new Exceptionf("Variable %s doesn't exist!", name);
      superEnv.set(name,newValue);
    } else {
      bindNameIfCallable(newValue,name);
      bindings.put(name,newValue);
    }
  }


  public void set(java.lang.String name, Primitive prm) throws Exception {
    set(name,new PrimitiveProcedure(name,prm));
  }


  ////////////////////////////////////////////////////////////////////////////
  // Define value
  public void define(java.lang.String name, Datum value) throws Exception {
    bindNameIfCallable(value,name);
    bindings.put(name,value);
  }
  

  public void define(java.lang.String name, Primitive prm) throws Exception {
    define(name,new PrimitiveProcedure(name,prm));
  }


  ////////////////////////////////////////////////////////////////////////////
  // Helper to Bind Names to Callables
  private void bindNameIfCallable(Datum d, java.lang.String name) throws Exception {
    if(d instanceof Procedure) {
      Procedure p = (Procedure)d;
      if(p.name.equals(Procedure.DEFAULT_NAME)) p.bindName(name);
    } else if(d instanceof Macro) {
      Macro m = (Macro)d;
      if(m.name.equals(Macro.DEFAULT_NAME)) m.bindName(name);
    }
  }
}