// Author: Jordan Randleman - Primitive.List
// Purpose:
//    Java primitives for list procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;

public class List {
  ////////////////////////////////////////////////////////////////////////////
  // Size Number Validation Helper
  public static boolean isValidSize(Datum d)throws Exception {
    if(!(d instanceof Type.Number)) return false;
    double value = ((Type.Number)d).value;
    return value >= 0.0 && value % 1 == 0.0;
  }


  ////////////////////////////////////////////////////////////////////////////
  // list
  public static class ConstructList implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      Datum d = new Type.Nil();
      for(int i = parameters.size()-1; i >= 0; --i)
        d = new Type.Pair(parameters.get(i),d);
      return d;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // list*
  public static class ListStar implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'list* received less than the 2 required args: %s", Exceptionf.profileArgs(parameters));
      Datum d = parameters.get(parameters.size()-1);
      for(int i = parameters.size()-2; i >= 0; --i)
        d = new Type.Pair(parameters.get(i),d);
      return d;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // append
  public static class Append implements Type.Primitive {
    private static Datum shallowCopy(Datum lis2) {
      if(!(lis2 instanceof Type.Pair)) return lis2;
      Type.Pair lis2Pair = (Type.Pair)lis2;
      return new Type.Pair(lis2Pair.car,shallowCopy(lis2Pair.cdr));
    }

    public static Datum binaryAppend(Datum lis1, Datum lis2) throws Exception {
      if(lis1 instanceof Type.Nil) {
        if(lis2 instanceof Type.Pair) return shallowCopy(lis2);
        return lis2;
      }
      Type.Pair lis1Pair = (Type.Pair)lis1;
      return new Type.Pair(lis1Pair.car,binaryAppend(lis1Pair.cdr,lis2));
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      int totalParameters = parameters.size();
      if(totalParameters == 0) return new Type.Nil();
      if(totalParameters == 1) return parameters.get(0);
      Datum lhs = parameters.get(0);
      for(int i = 1; i < totalParameters; ++i) {
        if(!Type.Pair.isList(lhs))
          throw new Exceptionf("'append can't append data to non-list %s", lhs.profile());
        lhs = binaryAppend(lhs,parameters.get(i));
      }
      return lhs;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // length
  public static class Length implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !Type.Pair.isList(parameters.get(0))) 
        throw new Exceptionf("'length didn't receive exactly 1 list: %s", Exceptionf.profileArgs(parameters));
      int count = 0;
      Datum iterator = parameters.get(0);
      while(iterator instanceof Type.Pair) {
        ++count;
        iterator = ((Type.Pair)iterator).cdr;
      }
      return new Type.Number((double)count);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // reverse
  public static class Reverse implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !Type.Pair.isList(parameters.get(0)))
        throw new Exceptionf("'reverse didn't receive exactly 1 list: %s", Exceptionf.profileArgs(parameters));
      Datum reversed = new Type.Nil();
      Datum iterator = parameters.get(0);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        reversed = new Type.Pair(iteratorPair.car,reversed);
        iterator = iteratorPair.cdr;
      }
      return reversed;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // map
  public static class Map implements Type.Primitive {
    // returns <null> if one of the lists was empty
    // lists assumed to begin at parameters.get(1)
    public static ArrayList<Datum> getCars(ArrayList<Datum> parameters, int totalParameters) throws Exception {
      ArrayList<Datum> cars = new ArrayList<Datum>();
      for(int i = 1; i < totalParameters; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Pair)) return null;
        cars.add(((Type.Pair)p).car);
      }
      return cars;
    }

    // lists assumed to begin at parameters.get(1)
    // PRECONDITION: All lists in <parameters> are still pairs
    public static void applyCdrs(ArrayList<Datum> parameters, int totalParameters) throws Exception {
      for(int i = 1; i < totalParameters; ++i)
        parameters.set(i,((Type.Pair)parameters.get(i)).cdr);
    }

    private static Datum mapRecur(Type.Procedure procedure, ArrayList<Datum> parameters, int totalParameters) throws Exception {
      ArrayList<Datum> carParams = getCars(parameters,totalParameters);
      if(carParams == null) return new Type.Nil(); // reached the end of one of the mapped lists
      applyCdrs(parameters,totalParameters);
      return new Type.Pair(procedure.callWith(Util.Runtime.globalEnvironment,carParams),mapRecur(procedure,parameters,totalParameters));
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      // Validation
      int totalParameters = parameters.size();
      if(totalParameters < 2)
        throw new Exceptionf("'map didn't receive at least 2 args (procedure & list): %s", Exceptionf.profileArgs(parameters));
      Datum procedure = parameters.get(0);
      if(!(procedure instanceof Type.Procedure)) 
        throw new Exceptionf("'map 1st arg %s isn't a procedure!", procedure.profile());
      for(int i = 1; i < totalParameters; ++i)
        if(!Type.Pair.isList(parameters.get(i))) 
          throw new Exceptionf("'map %dth arg %s isn't a list!", i+1, parameters.get(i).profile());
      // Implementation
      return mapRecur((Type.Procedure)procedure,parameters,totalParameters);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // for-each
  public static class ForEach implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      // Validation
      int totalParameters = parameters.size();
      if(totalParameters < 2)
        throw new Exceptionf("'for-each didn't receive at least 2 args (procedure & list): %s", Exceptionf.profileArgs(parameters));
      Datum procedure = parameters.get(0);
      if(!(procedure instanceof Type.Procedure)) 
        throw new Exceptionf("'for-each 1st arg %s isn't a procedure!", procedure.profile());
      for(int i = 1; i < totalParameters; ++i)
        if(!Type.Pair.isList(parameters.get(i))) 
          throw new Exceptionf("'for-each %dth arg %s isn't a list!", i+1, parameters.get(i).profile());
      // Implementation
      while(true) {
        ArrayList<Datum> carParams = Map.getCars(parameters,totalParameters);
        if(carParams == null) return new Type.Void();
        Map.applyCdrs(parameters,totalParameters);
        ((Type.Procedure)procedure).callWith(Util.Runtime.globalEnvironment,carParams);
      }
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // filter
  public static class Filter implements Type.Primitive {
    public static Datum filterRecur(Type.Procedure procedure, Datum lis) throws Exception {
      if(!(lis instanceof Type.Pair)) return new Type.Nil();
      ArrayList<Datum> arg = new ArrayList<Datum>();
      Type.Pair lisPair = (Type.Pair)lis;
      arg.add(lisPair.car);
      if(procedure.callWith(Util.Runtime.globalEnvironment,arg).isTruthy())
        return new Type.Pair(lisPair.car,filterRecur(procedure,lisPair.cdr));
      return filterRecur(procedure,lisPair.cdr);
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      // Validation
      int totalParameters = parameters.size();
      if(totalParameters != 2)
        throw new Exceptionf("'filter didn't receive exactly 2 args (procedure & list): %s", Exceptionf.profileArgs(parameters));
      Datum procedure = parameters.get(0);
      Datum lis = parameters.get(1);
      if(!(procedure instanceof Type.Procedure)) 
        throw new Exceptionf("'filter 1st arg %s isn't a procedure!", procedure.profile());
      if(!Type.Pair.isList(lis)) 
        throw new Exceptionf("'filter 2nd arg %s isn't a list!", lis.profile());
      // Implementation
      return filterRecur((Type.Procedure)procedure,lis);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // fold
  public static class Fold implements Type.Primitive {
    public static void validateFoldOperation(ArrayList<Datum> parameters, String callerName) throws Exception {
      if(parameters.size() != 3)
        throw new Exceptionf("'%s didn't receive exactly 3 args: %s", callerName, Exceptionf.profileArgs(parameters));
      if(!(parameters.get(0) instanceof Type.Procedure))
        throw new Exceptionf("'%s 1st arg %s isn't a procedure!", callerName, parameters.get(0).profile());
      if(!Type.Pair.isList(parameters.get(2)))
        throw new Exceptionf("'%s 3rd arg %s isn't a list!", callerName, parameters.get(2).profile());
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      validateFoldOperation(parameters,"fold");
      Type.Procedure procedure = (Type.Procedure)parameters.get(0);
      Datum acc = parameters.get(1);
      Datum iterator = parameters.get(2);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        ArrayList<Datum> args = new ArrayList<Datum>();
        args.add(acc);
        args.add(iteratorPair.car);
        acc = procedure.callWith(Util.Runtime.globalEnvironment,args);
        iterator = iteratorPair.cdr;
      }
      return acc;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // fold-right
  public static class FoldRight implements Type.Primitive {
    private static Datum foldRightRecur(Type.Procedure procedure, Datum acc, Datum lis) throws Exception {
      if(lis instanceof Type.Nil) return acc;
      Type.Pair lisPair = (Type.Pair)lis;
      ArrayList<Datum> args = new ArrayList<Datum>();
      args.add(lisPair.car);
      args.add(foldRightRecur(procedure,acc,lisPair.cdr));
      return procedure.callWith(Util.Runtime.globalEnvironment,args);
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      Fold.validateFoldOperation(parameters,"fold-right");
      return foldRightRecur((Type.Procedure)parameters.get(0),parameters.get(1),parameters.get(2));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // last
  public static class Last implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !Type.Pair.isList(parameters.get(0)) || !(parameters.get(0) instanceof Type.Pair))
        throw new Exceptionf("'last didn't receive exactly 1 non-empty list: %s", Exceptionf.profileArgs(parameters));
      Datum iterator = parameters.get(0);
      while(true) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(iteratorPair.cdr instanceof Type.Nil) return iteratorPair.car;
        iterator = iteratorPair.cdr;
      }
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // init
  public static class Init implements Type.Primitive {
    private Datum initRecur(Datum lis) throws Exception {
      Type.Pair lisPair = (Type.Pair)lis;
      if(lisPair.cdr instanceof Type.Nil) return new Type.Nil();
      return new Type.Pair(lisPair.car,initRecur(lisPair.cdr));
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !Type.Pair.isList(parameters.get(0)) || !(parameters.get(0) instanceof Type.Pair))
        throw new Exceptionf("'init didn't receive exactly 1 non-empty list: %s", Exceptionf.profileArgs(parameters));
      return initRecur(parameters.get(0));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // ref
  public static class Ref implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'ref didn't receive exactly 2 args (list & index): %s", Exceptionf.profileArgs(parameters));
      Datum lis = parameters.get(0);
      Datum index = parameters.get(1);
      if(!(lis instanceof Type.Pair)) 
        throw new Exceptionf("'ref 1st arg %s isn't a non-empty list!", lis.profile());
      if(!isValidSize(index)) 
        throw new Exceptionf("'ref 2nd arg %s isn't a non-negative integer!", index.profile());
      double indexValue = ((Type.Number)index).value;
      for(double count = 0; lis instanceof Type.Pair; ++count, lis = ((Type.Pair)lis).cdr)
        if(count == indexValue)
          return ((Type.Pair)lis).car;
      throw new Exceptionf("'ref index %f is out of bounds for list %s", indexValue, parameters.get(0).write());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // sublist
  public static class Sublist implements Type.Primitive {
    private static double getSublistLength(ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 2) return Double.POSITIVE_INFINITY; // defaults till the end of the list
      Datum endIndex = parameters.get(2);
      if(!isValidSize(endIndex)) 
        throw new Exceptionf("'sublist 3rd arg %s isn't a non-negative integer!", endIndex.profile());
      return ((Type.Number)endIndex).value;
    }

    private static Datum sublistRecur(Datum lis, double count, double startIndex, double length) throws Exception {
      if(lis instanceof Type.Nil || count >= length) return new Type.Nil();
      Type.Pair lisPair = (Type.Pair)lis;
      if(count < startIndex) return sublistRecur(lisPair.cdr,count+1,startIndex,length);
      return new Type.Pair(lisPair.car,sublistRecur(lisPair.cdr,count+1,startIndex,length));
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2 && parameters.size() != 3)
        throw new Exceptionf("'sublist didn't receive 2 or 3 args (list, start-index, & optional length): %s", Exceptionf.profileArgs(parameters));
      double length = getSublistLength(parameters);
      Datum lis = parameters.get(0);
      Datum startIndex = parameters.get(1);
      if(!Type.Pair.isList(lis)) 
        throw new Exceptionf("'sublist 1st arg %s isn't a list!", lis.profile());
      if(!isValidSize(startIndex)) 
        throw new Exceptionf("'sublist 2nd arg %s isn't a non-negative integer!", startIndex.profile());
      double startIndexValue = ((Type.Number)startIndex).value;
      return sublistRecur(lis,0,startIndexValue,length+startIndexValue);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // memq
  public static class Memq implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'memq didn't receive exactly 2 args (object & list): %s", Exceptionf.profileArgs(parameters));
      if(!Type.Pair.isList(parameters.get(1))) 
        throw new Exceptionf("'memq 2nd arg %s isn't a list!", parameters.get(1).profile());
      Datum obj = parameters.get(0);
      Datum iterator = parameters.get(1);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(iteratorPair.car.eq(obj))
          return iteratorPair;
        iterator = iteratorPair.cdr;
      }
      return new Type.Boolean(false);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // member
  public static class Member implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'member didn't receive exactly 2 args (object & list): %s", Exceptionf.profileArgs(parameters));
      if(!Type.Pair.isList(parameters.get(1))) 
        throw new Exceptionf("'member 2nd arg %s isn't a list!", parameters.get(1).profile());
      Datum obj = parameters.get(0);
      Datum iterator = parameters.get(1);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(iteratorPair.car.equals(obj))
          return iteratorPair;
        iterator = iteratorPair.cdr;
      }
      return new Type.Boolean(false);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // assq
  public static class Assq implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'assq didn't receive exactly 2 args (key & alist): %s", Exceptionf.profileArgs(parameters));
      if(!Type.Pair.isList(parameters.get(1))) 
        throw new Exceptionf("'assq 2nd arg %s isn't an alist (list of key-value pair lists)!", parameters.get(1).profile());
      Datum key = parameters.get(0);
      Datum iterator = parameters.get(1);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(!(iteratorPair.car instanceof Type.Pair) || !(((Type.Pair)iteratorPair.car).cdr instanceof Type.Pair))
          throw new Exceptionf("'assq 2nd arg %s isn't an alist (list of key-value pair lists)!", parameters.get(1).profile());
        Type.Pair innerList = (Type.Pair)iteratorPair.car;
        if(innerList.car.eq(key))
          return ((Type.Pair)innerList.cdr).car;
        iterator = iteratorPair.cdr;
      }
      return new Type.Boolean(false);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // assoc
  public static class Assoc implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'assoc didn't receive exactly 2 args (key & alist): %s", Exceptionf.profileArgs(parameters));
      if(!Type.Pair.isList(parameters.get(1))) 
        throw new Exceptionf("'assoc 2nd arg %s isn't an alist (list of key-value pair lists)!", parameters.get(0).profile());
      Datum key = parameters.get(0);
      Datum iterator = parameters.get(1);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(!(iteratorPair.car instanceof Type.Pair) || !(((Type.Pair)iteratorPair.car).cdr instanceof Type.Pair))
          throw new Exceptionf("'assoc 2nd arg %s isn't an alist (list of key-value pair lists)!", parameters.get(0).profile());
        Type.Pair innerList = (Type.Pair)iteratorPair.car;
        if(innerList.car.equals(key))
          return ((Type.Pair)innerList.cdr).car;
        iterator = iteratorPair.cdr;
      }
      return new Type.Boolean(false);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // sort
  public static class Sort implements Type.Primitive {
    public static Datum sortList(Type.Procedure procedure, Datum lis) throws Exception {
      if(lis instanceof Type.Nil) return new Type.Nil();
      // Generate procedures to pass to the filter functions
      ArrayList<String> parameters = new ArrayList<String>();
      parameters.add("item");
      Type.Pair lisPair = (Type.Pair)lis;
      Datum lessThanBody = Type.Pair.List(procedure,new Type.Symbol("item"),lisPair.car);
      Datum greaterThanBody = Type.Pair.List(procedure,lisPair.car,new Type.Symbol("item"));
      Type.Procedure lessThanProcedure = new Type.CompoundProcedure(parameters,lessThanBody,Util.Runtime.globalEnvironment,false);
      Type.Procedure greaterThanProcedure = new Type.CompoundProcedure(parameters,greaterThanBody,Util.Runtime.globalEnvironment,false);
      // Quicksort!
      return Append.binaryAppend(sortList(procedure,Filter.filterRecur(lessThanProcedure,lisPair.cdr)),
                                 new Type.Pair(lisPair.car,sortList(procedure,Filter.filterRecur(greaterThanProcedure,lisPair.cdr))));
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'sort didn't receive exactly 2 args (predicate & list): %s", Exceptionf.profileArgs(parameters));
      Datum procedure = parameters.get(0);
      Datum lis = parameters.get(1);
      if(!(procedure instanceof Type.Procedure)) 
        throw new Exceptionf("'sort 1st arg %s isn't a predicate procedure!", procedure.profile());
      if(!Type.Pair.isList(lis)) 
        throw new Exceptionf("'sort 2nd arg %s isn't a list!", lis.profile());
      return sortList((Type.Procedure)procedure,lis);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // sorted?
  public static class IsSorted implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      // Validation
      if(parameters.size() != 2)
        throw new Exceptionf("'sorted? didn't receive exactly 2 args (predicate & list): %s", Exceptionf.profileArgs(parameters));
      Datum procedure = parameters.get(0);
      Datum lis = parameters.get(1);
      if(!(procedure instanceof Type.Procedure)) 
        throw new Exceptionf("'sorted? 1st arg %s isn't a predicate procedure!", procedure.profile());
      if(!Type.Pair.isList(lis)) 
        throw new Exceptionf("'sorted? 2nd arg %s isn't a list!", lis.profile());
      // Implementation
      if(lis instanceof Type.Nil || ((Type.Pair)lis).cdr instanceof Type.Nil) return new Type.Boolean(true);
      Type.Pair lisPair = (Type.Pair)lis;
      while(lisPair.cdr instanceof Type.Pair) {
        Type.Pair lisPairCdr = (Type.Pair)lisPair.cdr;
        ArrayList<Datum> args = new ArrayList<Datum>();
        args.add(lisPair.car);
        args.add(lisPairCdr.car);
        if(!((Type.Procedure)procedure).callWith(Util.Runtime.globalEnvironment,args).isTruthy())
          return new Type.Boolean(false);
        lisPair = lisPairCdr;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // list?
  public static class IsList implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'list? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(Type.Pair.isList(parameters.get(0)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // list*?
  public static class IsListStar implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'list*? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum iterator = parameters.get(0);
      if(!(iterator instanceof Type.Pair)) return new Type.Boolean(false);
      while(iterator instanceof Type.Pair) iterator = ((Type.Pair)iterator).cdr;
      return new Type.Boolean(!(iterator instanceof Type.Nil));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // circular-list?
  public static class IsCircularList implements Type.Primitive {
    // Floyd's loop detection algorithm
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'circular-list? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum slow = parameters.get(0);
      if(!(slow instanceof Type.Pair)) return new Type.Boolean(false);
      Datum fast = ((Type.Pair)slow).cdr;
      while(fast instanceof Type.Pair && fast != slow) {
        Type.Pair fastPair = (Type.Pair)fast;
        if(!(fastPair.cdr instanceof Type.Pair) || !(((Type.Pair)fastPair.cdr).cdr instanceof Type.Pair))
          return new Type.Boolean(false);
        fast = ((Type.Pair)fastPair.cdr).cdr;
        slow = ((Type.Pair)slow).cdr;
      }
      return new Type.Boolean(fast == slow);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // alist?
  public static class IsAlist implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'alist? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum iterator = parameters.get(0);
      if(!Type.Pair.isList(iterator)) return new Type.Boolean(false);
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(!(iteratorPair.car instanceof Type.Pair) || !(((Type.Pair)iteratorPair.car).cdr instanceof Type.Pair))
          return new Type.Boolean(false);
        iterator = iteratorPair.cdr;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // null?
  public static class IsNull implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'null? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Nil);
    }
  }
}