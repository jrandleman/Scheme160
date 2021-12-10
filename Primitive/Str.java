// Author: Jordan Randleman - Primitive.Str
// Purpose:
//    Java primitives for string procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class Str {
  ////////////////////////////////////////////////////////////////////////////
  // string-length
  public static class StringLength implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string-length didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      return new Type.Number(((Type.String)parameters.get(0)).value.length());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-empty?
  public static class IsStringEmpty implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string-empty? didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(((Type.String)parameters.get(0)).value.length() == 0);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-reverse
  public static class StringReverse implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string-reverse didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      String str = ((Type.String)parameters.get(0)).value;
      StringBuilder sb = new StringBuilder();
      for(int i = str.length()-1; i >= 0; --i)
        sb.append(str.charAt(i));
      return new Type.String(sb.toString());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-append
  public static class StringAppend implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      StringBuilder sb = new StringBuilder();
      for(Datum p : parameters) {
        if(!(p instanceof Type.String))
          throw new Exceptionf("'string-append received a non-string arg: %s", Exceptionf.profileArgs(parameters));
        sb.append(((Type.String)p).value);
      }
      return new Type.String(sb.toString());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-ref
  public static class StringRef implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2)
        throw new Exceptionf("'string-ref expects exactly 2 args (string & index): %s", Exceptionf.profileArgs(parameters));
      Datum str = parameters.get(0);
      Datum index = parameters.get(1);
      if(!(str instanceof Type.String))
        throw new Exceptionf("'string-ref 1st arg %s isn't a string!", str.profile());
      if(!List.isValidSize(index))
        throw new Exceptionf("'string-ref 2nd arg %s isn't a non-negative integer!", index.profile());
      double indexValue = ((Type.Number)index).value;
      String strValue = ((Type.String)str).value;
      if(indexValue >= strValue.length())
        throw new Exceptionf("'string-ref index %f exceeds length of string %s", indexValue, str.write());
      return new Type.String(String.valueOf(strValue.charAt((int)indexValue)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // substring
  public static class Substring implements Type.Primitive {
    private static double getSubstringLength(ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 2) return Double.POSITIVE_INFINITY; // defaults till the end of the list
      Datum endIndex = parameters.get(2);
      if(!List.isValidSize(endIndex)) 
        throw new Exceptionf("'substring 3rd arg %s isn't a non-negative integer!", endIndex.profile());
      return ((Type.Number)endIndex).value;
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2 && parameters.size() != 3)
        throw new Exceptionf("'substring didn't receive 2 or 3 args (list, start-index, & optional length): %s", Exceptionf.profileArgs(parameters));
      double substringLength = getSubstringLength(parameters);
      Datum str = parameters.get(0);
      Datum startIndex = parameters.get(1);
      if(!(str instanceof Type.String)) 
        throw new Exceptionf("'substring 1st arg %s isn't a string!", str.profile());
      if(!List.isValidSize(startIndex)) 
        throw new Exceptionf("'substring 2nd %s arg isn't a non-negative integer!", startIndex.profile());
      double startIndexValue = ((Type.Number)startIndex).value;
      String strValue = ((Type.String)str).value;
      if(startIndexValue >= strValue.length() || substringLength == 0) 
        return new Type.String("");
      if(substringLength == Double.POSITIVE_INFINITY || substringLength+startIndexValue >= strValue.length())
        return new Type.String(strValue.substring((int)startIndexValue));
      return new Type.String(strValue.substring((int)startIndexValue,(int)(substringLength+startIndexValue)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-upcase
  public static class StringUpcase implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string-upcase didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(((Type.String)parameters.get(0)).value.toUpperCase());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-downcase
  public static class StringDowncase implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string-downcase didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(((Type.String)parameters.get(0)).value.toLowerCase());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-replace
  public static class StringReplace implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 3 || !(parameters.get(0) instanceof Type.String) || 
                                   !(parameters.get(1) instanceof Type.String) || 
                                   !(parameters.get(2) instanceof Type.String)) {
        throw new Exceptionf("'string-replace didn't receive exactly 3 strings: %s", Exceptionf.profileArgs(parameters));
      }
      return new Type.String(((Type.String)parameters.get(0)).value.replaceAll(((Type.String)parameters.get(1)).value,
                                                                                Matcher.quoteReplacement(((Type.String)parameters.get(2)).value)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-trim
  public static class StringTrim implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'string-trim didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(((Type.String)parameters.get(0)).value.trim());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-contains
  public static class StringContains implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2 || !(parameters.get(0) instanceof Type.String) || !(parameters.get(1) instanceof Type.String)) 
        throw new Exceptionf("'string-contains didn't receive exactly 2 strings: %s", Exceptionf.profileArgs(parameters));
      double result = (double)((Type.String)parameters.get(0)).value.indexOf(((Type.String)parameters.get(1)).value);
      if(result < 0) return new Type.Boolean(false);
      return new Type.Number(result);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-contains-right
  public static class StringContainsRight implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2 || !(parameters.get(0) instanceof Type.String) || !(parameters.get(1) instanceof Type.String)) 
        throw new Exceptionf("'string-contains-right didn't receive exactly 2 strings: %s", Exceptionf.profileArgs(parameters));
      double result = (double)((Type.String)parameters.get(0)).value.lastIndexOf(((Type.String)parameters.get(1)).value);
      if(result < 0) return new Type.Boolean(false);
      return new Type.Number(result);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-join
  public static class StringJoin implements Type.Primitive {
    private static String getJoinerString(ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 1) return "";
      Datum joiner = parameters.get(1);
      if(!(joiner instanceof Type.String))
        throw new Exceptionf("'string-join 2nd arg isn't a string: %s", Exceptionf.profileArgs(parameters));
      return ((Type.String)joiner).value;
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 && parameters.size() != 2) 
        throw new Exceptionf("'string-join didn't receive exactly 1 or 2 args: %s", Exceptionf.profileArgs(parameters));
      StringBuilder sb = new StringBuilder();
      String joiner = getJoinerString(parameters);
      Datum iterator = parameters.get(0);
      if(!Type.Pair.isList(iterator))
        throw new Exceptionf("'string-join 1st arg %s isn't a string list!", parameters.get(0).profile());
      while(iterator instanceof Type.Pair) {
        Type.Pair iteratorPair = (Type.Pair)iterator;
        if(!(iteratorPair.car instanceof Type.String))
          throw new Exceptionf("'string-join 1st arg %s isn't a string list!", parameters.get(0).profile());
        sb.append(((Type.String)iteratorPair.car).value);
        if(!(iteratorPair.cdr instanceof Type.Nil))
          sb.append(joiner);
        iterator = iteratorPair.cdr;
      }
      return new Type.String(sb.toString());
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-split
  public static class StringSplit implements Type.Primitive {
    private static String getSplitterString(ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() == 1) return "";
      Datum splitter = parameters.get(1);
      if(!(splitter instanceof Type.String))
        throw new Exceptionf("'string-split 2nd arg isn't a string: %s", Exceptionf.profileArgs(parameters));
      return ((Type.String)splitter).value;
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if((parameters.size() != 1 && parameters.size() != 2) || !(parameters.get(0) instanceof Type.String))
        throw new Exceptionf("'string-split didn't receive exactly 1 or 2 strings: %s", Exceptionf.profileArgs(parameters));
      String[] strArray = ((Type.String)parameters.get(0)).value.split(getSplitterString(parameters));
      Datum strList = new Type.Nil();
      for(int i = strArray.length-1; i >= 0; --i)
        strList = new Type.Pair(new Type.String(strArray[i]),strList);
      return strList;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string=?
  public static class StringEquals implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string=? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string=? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string=? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareTo(strValue) != 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string<?
  public static class StringLessThan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string<? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string<? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string<? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareTo(strValue) >= 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string>?
  public static class StringGreaterThan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string>? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string>? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string>? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareTo(strValue) <= 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string<=?
  public static class StringLessThanOrEqualTo implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string<=? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string<=? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string<=? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareTo(strValue) > 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string>=?
  public static class StringGreaterThanOrEqualTo implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string>=? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string>=? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string>=? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareTo(strValue) < 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-ci=?
  public static class StringCiEquals implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string-ci=? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string-ci=? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string-ci=? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareToIgnoreCase(strValue) != 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-ci<?
  public static class StringCiLessThan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string-ci<? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string-ci<? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string-ci<? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareToIgnoreCase(strValue) >= 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-ci>?
  public static class StringCiGreaterThan implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string-ci>? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string-ci>? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string-ci>? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareToIgnoreCase(strValue) <= 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-ci<=?
  public static class StringCiLessThanOrEqualTo implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string-ci<=? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string-ci<=? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string-ci<=? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareToIgnoreCase(strValue) > 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string-ci>=?
  public static class StringCiGreaterThanOrEqualTo implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() < 2) 
        throw new Exceptionf("'string-ci>=? expects at least 2 args: %s", Exceptionf.profileArgs(parameters));
      Datum p = parameters.get(0);
      if(!(p instanceof Type.String))
        throw new Exceptionf("'string-ci>=? invalid non-string arg %s recieved!", p.profile());
      String lastValue = ((Type.String)p).value;
      for(int i = 1, n = parameters.size(); i < n; ++i) {
        Datum str = parameters.get(i);
        if(!(str instanceof Type.String))
          throw new Exceptionf("'string-ci>=? invalid non-string arg %s recieved!", str.profile());
        String strValue = ((Type.String)str).value;
        if(lastValue.compareToIgnoreCase(strValue) < 0) return new Type.Boolean(false);
        lastValue = strValue;
      }
      return new Type.Boolean(true);
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // string?
  public static class IsString implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'string? didn't receive exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(parameters.get(0) instanceof Type.String);
    }
  }
}