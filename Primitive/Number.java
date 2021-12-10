// Author: Jordan Randleman - Primitive.Number
// Purpose:
//    Java primitives for number procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;
import java.util.Random;

public class Number {
  ////////////////////////////////////////////////////////////////////////////
  // +
  public static class Plus implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) throw new Exceptionf("'+ expects at least 1 arg: %s", Exceptionf.profileArgs(parameters));
      double sum = 0.0;
      for(Datum p : parameters) {
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'+ invalid non-numeric arg %s recieved!", p.profile());
        sum += ((Type.Number)p).value;
      }
      return new Type.Number(sum);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // -
  public static class Minus implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) throw new Exceptionf("'- expects at least 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum firstparam = parameters.get(0);
      if(!(firstparam instanceof Type.Number))
        throw new Exceptionf("'- invalid non-numeric arg %s recieved!", firstparam.profile());
      if(parameters.size() == 1) 
        return new Type.Number(-((Type.Number)firstparam).value);
      double diff = ((Type.Number)firstparam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'- invalid non-numeric arg %s recieved!", p.profile());
        diff -= ((Type.Number)p).value;
      }
      return new Type.Number(diff);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // *
  public static class Multiply implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) throw new Exceptionf("'* expects at least 1 arg: %s", Exceptionf.profileArgs(parameters));
      double product = 1.0;
      for(Datum p : parameters) {
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'* invalid non-numeric arg %s recieved!", p.profile());
        product *= ((Type.Number)p).value;
      }
      return new Type.Number(product);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // /
  public static class Divide implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) throw new Exceptionf("'/ expects at least 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum firstparam = parameters.get(0);
      if(!(firstparam instanceof Type.Number))
        throw new Exceptionf("'/ invalid non-numeric arg %s recieved!", firstparam.profile());
      if(parameters.size() == 1) 
        return new Type.Number(1.0/((Type.Number)firstparam).value);
      double div = ((Type.Number)firstparam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'/ invalid non-numeric arg %s recieved!", p.profile());
        div /= ((Type.Number)p).value;
      }
      return new Type.Number(div);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // =
  public static class Equals implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) throw new Exceptionf("'= expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum firstParam = parameters.get(0);
      if(!(firstParam instanceof Type.Number))
        throw new Exceptionf("'= invalid non-numeric arg %s recieved!", firstParam.profile());
      double lastValue = ((Type.Number)firstParam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'= invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(lastValue != pValue) return new Type.Boolean(false);
        lastValue = pValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // <
  public static class LessThan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) throw new Exceptionf("'< expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum firstParam = parameters.get(0);
      if(!(firstParam instanceof Type.Number))
        throw new Exceptionf("'< invalid non-numeric arg %s recieved!", firstParam.profile());
      double lastValue = ((Type.Number)firstParam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'< invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(lastValue >= pValue) return new Type.Boolean(false);
        lastValue = pValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // >
  public static class GreaterThan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) throw new Exceptionf("'> expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum firstParam = parameters.get(0);
      if(!(firstParam instanceof Type.Number))
        throw new Exceptionf("'> invalid non-numeric arg %s recieved!", firstParam.profile());
      double lastValue = ((Type.Number)firstParam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'> invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(lastValue <= pValue) return new Type.Boolean(false);
        lastValue = pValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // <=
  public static class LessThanOrEqualTo implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) throw new Exceptionf("'<= expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum firstParam = parameters.get(0);
      if(!(firstParam instanceof Type.Number))
        throw new Exceptionf("'<= invalid non-numeric arg %s recieved!", firstParam.profile());
      double lastValue = ((Type.Number)firstParam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'<= invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(lastValue > pValue) return new Type.Boolean(false);
        lastValue = pValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // >=
  public static class GreaterThanOrEqualTo implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) throw new Exceptionf("'>= expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum firstParam = parameters.get(0);
      if(!(firstParam instanceof Type.Number))
        throw new Exceptionf("'>= invalid non-numeric arg %s recieved!", firstParam.profile());
      double lastValue = ((Type.Number)firstParam).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'>= invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(lastValue < pValue) return new Type.Boolean(false);
        lastValue = pValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // expt
  public static class Expt implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) throw new Exceptionf("'expt expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum firstparam = parameters.get(parameters.size()-1);
      if(!(firstparam instanceof Type.Number))
        throw new Exceptionf("'expt invalid non-numeric arg %s recieved!", firstparam.profile());
      double powVal = ((Type.Number)firstparam).value;
      for(int i = parameters.size()-2; i >= 0; --i) {
        Datum p = parameters.get(i);
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'expt invalid non-numeric arg %s recieved!", p.profile());
        powVal = Math.pow(((Type.Number)p).value,powVal);
      }
      return new Type.Number(powVal);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // exp
  public static class Exp implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'exp expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'exp invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.exp(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // log
  public static class Log implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'log expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'log invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.log(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // sqrt
  public static class Sqrt implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'sqrt expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'sqrt invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.sqrt(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // abs
  public static class Abs implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'abs expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'abs invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.abs(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // min
  public static class Min implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) throw new Exceptionf("'min expects at least 1 arg: %s", Exceptionf.profileArgs(parameters));
      double min = Double.POSITIVE_INFINITY;
      for(Datum p : parameters) {
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'min invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(min > pValue) min = pValue;
      }
      return new Type.Number(min);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // max
  public static class Max implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 0) throw new Exceptionf("'max expects at least 1 arg: %s", Exceptionf.profileArgs(parameters));
      double max = Double.NEGATIVE_INFINITY;
      for(Datum p : parameters) {
        if(!(p instanceof Type.Number))
          throw new Exceptionf("'max invalid non-numeric arg %s recieved!", p.profile());
        double pValue = ((Type.Number)p).value;
        if(max < pValue) max = pValue;
      }
      return new Type.Number(max);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // quotient
  public static class Quotient implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2) throw new Exceptionf("'quotient expects exactly 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum lhs = parameters.get(0);
      if(!(lhs instanceof Type.Number))
        throw new Exceptionf("'quotient invalid non-numeric 1st arg %s recieved!", lhs.profile());
      Datum rhs = parameters.get(1);
      if(!(rhs instanceof Type.Number))
        throw new Exceptionf("'quotient invalid non-numeric 2nd arg %s recieved!", rhs.profile());
      double quo = ((Type.Number)lhs).value/((Type.Number)rhs).value;
      if(quo < 0) return new Type.Number(Math.ceil(quo));
      return new Type.Number(Math.floor(quo));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // remainder
  public static class Remainder implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2) throw new Exceptionf("'remainder expects exactly 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum lhs = parameters.get(0);
      if(!(lhs instanceof Type.Number))
        throw new Exceptionf("'remainder invalid non-numeric 1st arg %s recieved!", lhs.profile());
      Datum rhs = parameters.get(1);
      if(!(rhs instanceof Type.Number))
        throw new Exceptionf("'remainder invalid non-numeric 2nd arg %s recieved!", rhs.profile());
      double lhsValue = ((Type.Number)lhs).value;
      double rhsValue = ((Type.Number)rhs).value;
      double quo = lhsValue/rhsValue;
      if(quo < 0) return new Type.Number(lhsValue - Math.ceil(quo) * rhsValue);
      return new Type.Number(lhsValue - Math.floor(quo) * rhsValue);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // round
  public static class Round implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'round expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'round invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.round(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // floor
  public static class Floor implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'floor expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'floor invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.floor(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // ceil
  public static class Ceiling implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'ceiling expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'ceiling invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.ceil(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // truncate
  public static class Truncate implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'truncate expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'truncate invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      if(nValue < 0) return new Type.Number(Math.ceil(nValue));
      return new Type.Number(Math.floor(nValue));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // number?
  public static class IsNumber implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'number? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.Number);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // integer?
  public static class IsInteger implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'integer? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'integer? invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Boolean(Math.abs(((Type.Number)n).value) % 1 == 0.0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // finite?
  public static class IsFinite implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'finite? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'finite? invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      return new Type.Boolean(nValue != Double.POSITIVE_INFINITY && 
                              nValue != Double.NEGATIVE_INFINITY && 
                              nValue == nValue); // x == x checks against x being NaN
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // infinite?
  public static class IsInfinite implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'infinite? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'infinite? invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      return new Type.Boolean(nValue == Double.POSITIVE_INFINITY || nValue == Double.NEGATIVE_INFINITY);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // nan?
  public static class IsNaN implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'nan? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'nan? invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      return new Type.Boolean(nValue != nValue); // x != x is ONLY true if x is NaN
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // odd?
  public static class IsOdd implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'odd? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'odd? invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Boolean(Math.abs(((Type.Number)n).value) % 2.0 == 1.0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // even?
  public static class IsEven implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'even? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'even? invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Boolean(Math.abs(((Type.Number)n).value) % 2.0 == 0.0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // positive?
  public static class IsPositive implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'positive? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'positive? invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Boolean(((Type.Number)n).value > 0.0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // negative?
  public static class IsNegative implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'negative? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'negative? invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Boolean(((Type.Number)n).value < 0.0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // zero?
  public static class IsZero implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'zero? expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'zero? invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Boolean(((Type.Number)n).value == 0.0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // sin
  public static class Sin implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'sin expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'sin invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.sin(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // cos
  public static class Cos implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'cos expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'cos invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.cos(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // tan
  public static class Tan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'tan expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'tan invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.tan(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // asin
  public static class Asin implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'asin expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'asin invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.asin(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // acos
  public static class Acos implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'acos expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'acos invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.acos(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // atan
  public static class Atan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 && parameters.size() != 2) 
        throw new Exceptionf("'atan expects exactly 1 or 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'atan invalid non-numeric arg %s recieved!", n.profile());
      if(parameters.size() == 1) return new Type.Number(Math.atan(((Type.Number)n).value));
      Datum n2 = parameters.get(1);
      if(!(n2 instanceof Type.Number))
        throw new Exceptionf("'atan invalid non-numeric 2nd arg %s recieved!", n2.profile());
      return new Type.Number(Math.atan2(((Type.Number)n).value,((Type.Number)n2).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // sinh
  public static class Sinh implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'sinh expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'sinh invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.sinh(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // cosh
  public static class Cosh implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'cosh expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'cosh invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.cosh(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // tanh
  public static class Tanh implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'tanh expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'tanh invalid non-numeric arg %s recieved!", n.profile());
      return new Type.Number(Math.tanh(((Type.Number)n).value));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // asinh
  public static class Asinh implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'asinh expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'asinh invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      return new Type.Number(Math.log(nValue + Math.sqrt(Math.pow(nValue,2) + 1)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // acosh
  public static class Acosh implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'acosh expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'acosh invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      return new Type.Number(Math.log(nValue + Math.sqrt(Math.pow(nValue,2) - 1)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // atanh
  public static class Atanh implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) throw new Exceptionf("'atanh expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      Datum n = parameters.get(0);
      if(!(n instanceof Type.Number))
        throw new Exceptionf("'atanh invalid non-numeric arg %s recieved!", n.profile());
      double nValue = ((Type.Number)n).value;
      return new Type.Number(0.5 * Math.log((1 + nValue) / (1 - nValue)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // random
  public static class Random implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 0) throw new Exceptionf("'random doesn't accept any args: %s", Exceptionf.profileArgs(parameters));
      return new Type.Number(Util.Runtime.prng.nextDouble());
    }
  }
}