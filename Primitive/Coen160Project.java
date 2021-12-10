// Author: Jordan Randleman - Primitive.Coen160Project
// Purpose:
//    Java primitives to support operations for COEN 160's final project.

package Primitive;
// Scheme160 interpreter imports
import Type.Datum;
import Type.Environment;
import Util.Exceptionf;
import java.util.ArrayList;
// GUI interfacing imports
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
// Dictionary Lookup imports
import java.net.URL;
import java.net.HttpURLConnection;

public class Coen160Project {

  // Synchronization wrapper class for fields shared between threads
  private static class SyncedFields {
    private boolean resetSession = false;
    private String inputWord = null;

    public synchronized void setResetSession(boolean value) {
      resetSession = value;
    }
    public synchronized boolean getResetSession() {
      return resetSession;
    }

    public synchronized void setInputWord(String value) {
      inputWord = value;
    }
    public synchronized String getInputWord() {
      return inputWord;
    }
  }


  // Text Fields
  private static JTextField usableLettersField = new JTextField("Usable Letters", 15);
  private static JTextField currentScoreField = new JTextField("0", 15);
  private static JTextField userWordInputField = new JTextField("Your Word Here", 30);

  // Wrapper object for synchronized access to thread-shared fields
  private static SyncedFields fields = new SyncedFields();


  ////////////////////////////////////////////////////////////////////////////
  // gui-get-input
  public static class GuiGetInput implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.Number)) 
        throw new Exceptionf("'gui-get-input didn't receive exactly 1 number arg: %s", Exceptionf.profileArgs(parameters));
      currentScoreField.setText(String.valueOf((int)((Type.Number)parameters.get(0)).value));
      String inputWord = null;
      boolean resetSession = false;
      while(inputWord == null && resetSession == false) {
        inputWord = fields.getInputWord();
        resetSession = fields.getResetSession();
      }
      fields.setResetSession(false);
      fields.setInputWord(null);
      if(resetSession) return new Type.Boolean(false);
      return new Type.String(inputWord);
    }
  }

  
  ////////////////////////////////////////////////////////////////////////////
  // gui-launch-session
  public static class GuiLaunchSession implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'gui-launch-session didn't receive exactly 1 string arg: %s", Exceptionf.profileArgs(parameters));
      usableLettersField.setText(((Type.String)parameters.get(0)).value);
      currentScoreField.setText("0");
      userWordInputField.setText("Enter your word here!");
      return new Type.Void();
    }
  }

  
  ////////////////////////////////////////////////////////////////////////////
  // gui-launch-window
  public static class GuiLaunchWindow implements Type.Primitive {
    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 0) 
        throw new Exceptionf("'gui-launch-window doesn't accept any args: %s", Exceptionf.profileArgs(parameters));

      // Create the window
      JFrame window = new JFrame();
      
      // Create our panels
      JPanel topPanel = new JPanel(new GridLayout(3, 0, 20, 40));
      JPanel row1Panel = new JPanel();
      
      // Set the usable letters & current score text fields to be immutable
      usableLettersField.setEditable(false);
      currentScoreField.setEditable(false);
      
      // Create our "reset session" button
      JButton resetSessionButton = new JButton("Click me for a New Game!");

      // Add a listener for user word inputs
      userWordInputField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fields.setInputWord(userWordInputField.getText());
        }
      });

      // Add a listener for session (usable letters) resetting
      resetSessionButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fields.setResetSession(true);
        }
      });

      // Add our first 2 user-immutable text areas to the first row panel
      row1Panel.add(usableLettersField);
      row1Panel.add(currentScoreField);

      // Add our panels to the top panel
      topPanel.add(row1Panel);
      topPanel.add(userWordInputField);
      topPanel.add(resetSessionButton);

      // Initialize & launch the window
      window.setContentPane(topPanel);
      window.pack();
      window.setTitle("COEN 160 Final Project");
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setVisible(true);

      return new Type.Void();
    }
  }

  
  ////////////////////////////////////////////////////////////////////////////
  // dictionary-valid-word?
  public static class DictionaryIsValidWord implements Type.Primitive {

    // Determine if <word> is a word via "https://dictionaryapi.dev"
    public static boolean isWord(String word) {
      try {
        URL url = new URL(String.format("https://api.dictionaryapi.dev/api/v2/entries/en/%s", word));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream(); // triggers "java.io.FileNotFoundException" if word definition URL isn't found
        return true;
      } catch(Exception e) {
        return false;
      }
    }

    public Datum callWith(Environment currentEnv, ArrayList<Datum> parameters) throws Exception {
      if(parameters.size() != 1 || !(parameters.get(0) instanceof Type.String)) 
        throw new Exceptionf("'dictionary-valid-word? didn't receive exactly 1 string arg: %s", Exceptionf.profileArgs(parameters));
      return new Type.Boolean(isWord(((Type.String)parameters.get(0)).value));
    }
  }
}
