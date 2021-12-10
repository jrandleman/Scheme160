// Author: Jordan Randleman - Primitive.Sys
// Purpose:
//    Java primitives for system operations & the <*argv*> list value.

package Primitive;
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;

public class Sys {
  ////////////////////////////////////////////////////////////////////////////
  // static field to determine the EXIT message
  public static final String EXIT_MESSAGE = "Bye!";


  ////////////////////////////////////////////////////////////////////////////
  // static field to hold command-line args
  public static Datum argv = new Type.Nil();


  ////////////////////////////////////////////////////////////////////////////
  // exit
  public static class Exit implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 0) throw new Exceptionf("'exit doesn't accept any args: %s", Exceptionf.profileArgs(parameters));
      // Print the exit msg iff in a REPL session
      if(Util.Runtime.inREPL) {
        if(!Primitive.IO.lastPrintedANewline) System.out.println("");
        System.out.println(EXIT_MESSAGE);
      }
      System.exit(0);
      return new Type.Void(); // never triggered
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // file-read
  public static class FileRead implements Type.Primitive {
    private static Datum convertReadExpressionsToReadExpression(ArrayList<Datum> contents) {
      if(contents.size() == 1) return contents.get(0);
      Datum expression = new Type.Nil();
      for(int i = contents.size()-1; i >= 0; --i)
        expression = new Type.Pair(contents.get(i),expression);
      return new Type.Pair(new Type.Symbol("begin"),expression);
    }

    public static String slurpFile(String filename, String callerName) throws Exception {
      try {
        return Files.readString(Path.of(filename));
      } catch(Exception e) {
        throw new Exceptionf("'%s couldn't read from file \"%s\"", filename, callerName);
      }
    }

    public static Datum readBuffer(String buffer) throws Exception {
      buffer = buffer.trim();
      if(buffer.length() == 0) return new Type.Void();
      ArrayList<Datum> contents = new ArrayList<Datum>();
      Integer n = buffer.length();
      Util.Pair<Datum,Integer> result = Util.Reader.read(buffer);
      contents.add(result.first);
      buffer = buffer.substring(result.second).trim();
      while(result.second != n && buffer.length() > 0) {
        n = buffer.length();
        result = Util.Reader.read(buffer);
        contents.add(result.first);
        buffer = buffer.substring(result.second).trim();
      }
      return convertReadExpressionsToReadExpression(contents);
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'file-read didn't receive exactly 1 filename string: %s", Exceptionf.profileArgs(parameters));
      return readBuffer(slurpFile(((Type.String)parameters.get(0)).value,"file-read"));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // file-read-string
  public static class FileReadString implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'file-read-string didn't receive exactly 1 filename string: %s", Exceptionf.profileArgs(parameters));
      return new Type.String(FileRead.slurpFile(((Type.String)parameters.get(0)).value,"file-read-string"));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // file-write
  public static class FileWrite implements Type.Primitive {
    public static void writeStringToFile(String filename, String str, String callerName) throws Exception {
      try {
        Files.writeString(Path.of(filename),str);
      } catch(Exception e) {
        throw new Exceptionf("'%s couldn't write to file \"%s\"", filename, callerName);
      }
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'file-write didn't receive exactly 2 args (filename string & datum): %s", Exceptionf.profileArgs(parameters));
      writeStringToFile(((Type.String)parameters.get(0)).value,parameters.get(1).write(),"file-write");
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // file-display
  public static class FileDisplay implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 2 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'file-display didn't receive exactly 2 args (filename string & datum): %s", Exceptionf.profileArgs(parameters));
      FileWrite.writeStringToFile(((Type.String)parameters.get(0)).value,parameters.get(1).display(),"file-display");
      return new Type.Void();
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // file-delete!
  public static class FileDelete implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'file-delete! didn't receive exactly 1 filename-string: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(Files.deleteIfExists(Path.of(((Type.String)parameters.get(0)).value)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // file?
  public static class IsFile implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'file? didn't receive exactly 1 string: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(Files.exists(Path.of(((Type.String)parameters.get(0)).value)));
    }
  }


  ////////////////////////////////////////////////////////////////////////////
  // load
  public static class Load implements Type.Primitive {
    public static Datum loadFileInEnvironment(Environment env, String filename) throws Exception {
      return Util.Core.eval(env,FileRead.readBuffer(FileRead.slurpFile(filename,"load")));
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'load didn't receive exactly 1 filename string: %s", Exceptionf.profileArgs(parameters));
      return loadFileInEnvironment(currentEnv,((Type.String)parameters.get(0)).value);
    }
  }
}