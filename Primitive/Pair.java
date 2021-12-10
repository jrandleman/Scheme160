// Author: Jordan Randleman - Primitive.Pair
// Purpose:
//    Java primitives for pair procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class Pair {
  ////////////////////////////////////////////////////////////////////////////
  // cons
  public static class Cons implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2) throw new Exceptionf("'cons didn't receive exactly 2 args: %s", Exceptionf.profileArgs(parameters));
      return new Type.Pair(parameters.get(0),parameters.get(1));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // car
  public static class Car implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'car didn't receive exactly 1 pair!: %s", Exceptionf.profileArgs(parameters));
      return ((Type.Pair)parameters.get(0)).car;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // cdr
  public static class Cdr implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'cdr didn't receive exactly 1 pair: %s", Exceptionf.profileArgs(parameters));
      return ((Type.Pair)parameters.get(0)).cdr;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // caar
  public static class Caar implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'caar didn't receive exactly 1 pair: %s", Exceptionf.profileArgs(parameters));
      Type.Pair pair = (Type.Pair)parameters.get(0);
      if(!(pair.car instanceof Type.Pair)) 
        throw new Exceptionf("'caar 1st 'car value %s isn't a pair!", pair.car.profile());
      return ((Type.Pair)pair.car).car;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // cadr
  public static class Cadr implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'cadr didn't receive exactly 1 pair: %s", Exceptionf.profileArgs(parameters));
      Type.Pair pair = (Type.Pair)parameters.get(0);
      if(!(pair.cdr instanceof Type.Pair)) 
        throw new Exceptionf("'cadr 1st 'cdr value %s isn't a pair!", pair.cdr.profile());
      return ((Type.Pair)pair.cdr).car;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // cdar
  public static class Cdar implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'cdar didn't receive exactly 1 pair: %s", Exceptionf.profileArgs(parameters));
      Type.Pair pair = (Type.Pair)parameters.get(0);
      if(!(pair.car instanceof Type.Pair)) 
        throw new Exceptionf("'cdar 1st 'car value %s isn't a pair!", pair.car.profile());
      return ((Type.Pair)pair.car).cdr;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // cddr
  public static class Cddr implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'cddr didn't receive exactly 1 pair: %s", Exceptionf.profileArgs(parameters));
      Type.Pair pair = (Type.Pair)parameters.get(0);
      if(!(pair.cdr instanceof Type.Pair)) 
        throw new Exceptionf("'cddr 1st 'cdr value %s isn't a pair!", pair.cdr.profile());
      return ((Type.Pair)pair.cdr).cdr;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // set-car!
  public static class SetCar implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2) 
        throw new Exceptionf("'set-car! didn't receive exactly 2 args: %s", Exceptionf.profileArgs(parameters));
      if(!(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'set-car! 1st arg %s isn't a pair!", parameters.get(0).profile());
      ((Type.Pair)parameters.get(0)).car = parameters.get(1);
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // set-cdr!
  public static class SetCdr implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2) 
        throw new Exceptionf("'set-cdr! didn't receive exactly 2 args: %s", Exceptionf.profileArgs(parameters));
      if(!(parameters.get(0) instanceof Type.Pair)) 
        throw new Exceptionf("'set-cdr! 1st arg %s isn't a pair!", parameters.get(0).profile());
      ((Type.Pair)parameters.get(0)).cdr = parameters.get(1);
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // pair?
  public static class IsPair implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'pair? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Pair);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // atom?
  public static class IsAtom implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'atom? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(!(parameters.get(0) instanceof Type.Pair));
    }
  }
}