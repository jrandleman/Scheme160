// Author: Jordan Randleman - Primitive.IO
// Purpose:
//    Java primitives for I/O procedures.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IO {
  ////////////////////////////////////////////////////////////////////////////
  // static field to track whether REPL should print a newline
  public static boolean lastPrintedANewline = false;


  ////////////////////////////////////////////////////////////////////////////
  // write
  public static class Write implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'write expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      System.out.print(parameters.get(0).write());
      lastPrintedANewline = false;
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // display
  public static class Display implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1) 
        throw new Exceptionf("'display expects exactly 1 arg: %s", Exceptionf.profileArgs(parameters));
      System.out.print(parameters.get(0).display());
      if(!(parameters.get(0) instanceof Type.String)) {
        lastPrintedANewline = false;
      } else {
        String str = ((Type.String)parameters.get(0)).value;
        lastPrintedANewline = str.charAt(str.length()-1) == '\n';
      }
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // newline
  public static class Newline implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 0) 
        throw new Exceptionf("'newline doesn't accept any args: %s", Exceptionf.profileArgs(parameters));
      System.out.println("");
      lastPrintedANewline = true;
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // read
  public static class Read implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 0) 
        throw new Exceptionf("'read doesn't accept any args: %s", Exceptionf.profileArgs(parameters));
      Datum readDatum = Util.Core.read(new BufferedReader(new InputStreamReader(System.in)));
      if(readDatum == null) return new Type.Void(); // EOF in a <read> call yields a <void> object
      return readDatum;
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // read-string
  public static class ReadString implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'read-string expects exactly 1 string arg: %s", Exceptionf.profileArgs(parameters));
      String readString = ((Type.String)parameters.get(0)).value.trim();
      if(readString.length() == 0) return new Type.Void(); // (read-string "") => <void>
      Util.Pair<Datum,Integer> result = Util.Reader.read(readString);
      String restOfString = readString.substring(result.second).trim();
      return new Type.Pair(result.first,new Type.String(restOfString));
    }
  }
}