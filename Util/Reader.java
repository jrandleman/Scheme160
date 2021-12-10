// Author: Jordan Randleman - Util.Reader
// Purpose:
//    Scheme reader -- given a string containing Scheme source code, parses such into a 
//    Scheme data structure that may be either manipulated as data or evaluated as code 
//    => Hence code IS data and data IS code!

package Util;
import Type.Datum;
import java.util.ArrayList;

public class Reader {
  ////////////////////////////////////////////////////////////////////////////
  // Exception Type Specialization
  public static class IncompleteException extends Exception {
    public IncompleteException() {}
    public IncompleteException(String msg) {super(msg);}
  }


  ////////////////////////////////////////////////////////////////////////////
  // General Helpers
  private static String writeString(String str) {
    return (new Type.String(str)).write();
  }


  private static boolean isDelimiter(char c) {
    return Character.isWhitespace(c) || c=='(' || c==')' || c=='"' || c==';';
  }


  ////////////////////////////////////////////////////////////////////////////
  // List Literal Parsing Helpers
  private static boolean isPeriodSymbol(Datum d) throws Exception {
    return d instanceof Type.Symbol && ((Type.Symbol)d).value.equals(".");
  }


  // Returns whether the list is dotted
  private static boolean validatePeriodSymbolPosition(ArrayList<Datum> arr) throws Exception {
    boolean isDotted = false;
    for(int i = 0, n = arr.size(); i < n; ++i) {
      if(isPeriodSymbol(arr.get(i))) {
        if(i+2 != n) throw new Exception("SCM160 READ ERROR: Invalid list literal: \".\" MUST be the penultimate symbol!");
        if(i > 0) isDotted = true; // (. <obj>) is treated as a list of 2 symbols
      }
    }
    return isDotted;
  }


  // Returns <arr> as a <Datum> scheme pair data structure
  private static Datum convertArrayListToSchemeList(ArrayList<Datum> arr) throws Exception {
    boolean isDotted = validatePeriodSymbolPosition(arr);
    if(isDotted) {
      arr.remove(arr.size()-2); // remove the dot
    } else {
      arr.add(new Type.Nil()); // add NIL at the end of the sequence
    }
    Datum list = arr.get(arr.size()-1);
    for(int i = arr.size()-2; i >= 0; --i)
      list = new Type.Pair(arr.get(i),list);
    return list;
  }


  // @param: <i> is where to start parsing
  // @return: pair of parsed list & position in <sourceCode> after the closing <)>
  private static Pair<Datum,Integer> parseListLiteral(String sourceCode, int i, int n, int parenCount) throws Exception {
    if(i == n)
      throw new IncompleteException("SCM160 READ ERROR: Incomplete list literal!");
    // parse NIL
    if(sourceCode.charAt(i) == ')') return new Pair<Datum,Integer>(new Type.Nil(),i+1);
    // parse PAIR
    ArrayList<Datum> listItems = new ArrayList<Datum>();
    Pair<Datum,Integer> parsedItem;
    while(i < n && sourceCode.charAt(i) != ')') {
      parsedItem = readLoop(sourceCode,i,parenCount);
      if(!(parsedItem.first instanceof Type.Void)) // if actually parsed something more than just whitespace & comments
        listItems.add(parsedItem.first);
      i = parsedItem.second;
    }
    if(i >= n)
      throw new IncompleteException(String.format("SCM160 READ ERROR: Invalid input \"%s\" terminated prior to being able to parse a datum!", writeString(sourceCode)));
    return new Pair<Datum,Integer>(convertArrayListToSchemeList(listItems),i+1);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Boolean Literal Parsing Helper
  // @param: <i> is where to start parsing
  // @return: pair of parsed boolean & position in <sourceCode> after the parsed boolean
  private static Pair<Datum,Integer> parseBooleanLiteral(String sourceCode, int i) {
    if(sourceCode.charAt(i+1) == 't')
      return new Pair<Datum,Integer>(new Type.Boolean(true),i+2);
    return new Pair<Datum,Integer>(new Type.Boolean(false),i+2);
  }


  ////////////////////////////////////////////////////////////////////////////
  // String Literal Parsing Helper
  // @param: <i> is where to start parsing
  // @return: pair of parsed string & position in <sourceCode> after the closing <">
  private static Pair<Datum,Integer> parseStringLiteral(String sourceCode, int i, int n) throws IncompleteException {
    int start = i; 
    StringBuilder sb = new StringBuilder();
    while(i < n) {
      if(sourceCode.charAt(i) == '"') {
        // verify a non-escaped quote
        int j = i-1, escapeCount = 0;
        while(j >= start && sourceCode.charAt(j) == '\\') {
          ++escapeCount;
          --j;
        }
        if(escapeCount % 2 == 0) { // non-escaped <">
          return new Pair<Datum,Integer>(new Type.String(StringParser.unescape(sb.toString())),i+1);
        } else { // escaped <">
          sb.append(sourceCode.charAt(i));
        }
      } else {
        sb.append(sourceCode.charAt(i));
      }
      ++i;
    }
    throw new IncompleteException("SCM160 READ ERROR: Unterminating string literal detected!");
  }


  ////////////////////////////////////////////////////////////////////////////
  // Number Literal Parsing Helper
  // @param: <i> is where to start parsing
  // @return: pair of parsed double & position in <sourceCode> after the parsed double
  private static Pair<Double,Integer> parseNumberLiteral(String sourceCode, int i, int n) {
    StringBuilder sb = new StringBuilder();
    while(i < n && !isDelimiter(sourceCode.charAt(i))) {
      sb.append(sourceCode.charAt(i));
      ++i;
    }
    try {
      return new Pair<Double,Integer>(Double.parseDouble(sb.toString()),i);
    } catch(Exception e) {
      return null;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // Symbol Literal Parsing Helper
  // @param: <i> is where to start parsing
  // @return: pair of parsed symbol & position in <sourceCode> after the parsed symbol
  //          => NOTE: returns a VOID object if at an "empty symbol" (IE if the reader was only given whitespace & comments)
  private static Pair<Datum,Integer> parseSymbolLiteral(String sourceCode, int i, int n) {
    StringBuilder sb = new StringBuilder();
    while(i < n && !isDelimiter(sourceCode.charAt(i))) {
      sb.append(sourceCode.charAt(i));
      ++i;
    }
    if(sb.length() == 0) return new Pair<Datum,Integer>(new Type.Void(),i);
    return new Pair<Datum,Integer>(new Type.Symbol(sb.toString()),i);
  }


  ////////////////////////////////////////////////////////////////////////////
  // Main Reader Loop
  private static Pair<Datum,Integer> readLoop(String sourceCode, int startIndex, int parenCount) throws Exception {

    for(int i = startIndex, n = sourceCode.length(); i < n; ++i) {

      // Account for paren scoping
      if(sourceCode.charAt(i) == '(') ++parenCount;
      else if(sourceCode.charAt(i) == ')') --parenCount;
      if(parenCount < 0) throw new Exception("SCM160 READ ERROR: Invalid parenthesis: found a ')' prior an associated '('!");

      // Ignore whitespace
      if(Character.isWhitespace(sourceCode.charAt(i))) continue;

      // Skip comments
      if(sourceCode.charAt(i) == ';') {
        while(i < n && sourceCode.charAt(i) != '\n') ++i;
        if(i == n) return new Pair<Datum,Integer>(new Type.Void(),i);
        continue;
      }

      // Parse List/Pair
      if(sourceCode.charAt(i) == '(') 
        return parseListLiteral(sourceCode,i+1,n,parenCount);

      // Parse Boolean Literals
      if(sourceCode.charAt(i) == '#' && i+1 < n && (sourceCode.charAt(i+1) == 't' || sourceCode.charAt(i+1) == 'f')) 
        return parseBooleanLiteral(sourceCode,i);

      // Parse String Literals
      if(sourceCode.charAt(i) == '"')
        return parseStringLiteral(sourceCode,i+1,n);

      // Parse Number Literals
      Pair<Double,Integer> numberParseObject = parseNumberLiteral(sourceCode,i,n);
      if(numberParseObject != null)
        return new Pair<Datum,Integer>(new Type.Number(numberParseObject.first),numberParseObject.second);

      // Parse Symbol Literals
      return parseSymbolLiteral(sourceCode,i,n);
    }
    throw new IncompleteException(String.format("SCM160 READ ERROR: Invalid input \"%s\" terminated prior to being able to parse a datum!", writeString(sourceCode)));
  }


  ////////////////////////////////////////////////////////////////////////////
  // Implementing "read": returns a pair: 
  //                      1. the read datum
  //                      2. the length of characters read from <sourceCode> to produce the read datum
  public static Pair<Datum,Integer> read(String sourceCode) throws Exception {
    return readLoop(sourceCode,0,0);
  }
}