/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 import javax.swing.event.*;
  
 public class GUITest extends JPanel
                       implements ListSelectionListener {
  
     private JButton fireButton;
  
     public GUITest() {
         super(new BorderLayout());
  
         fireButton = new JButton("Call normal java method");
         fireButton.setActionCommand("test");
         fireButton.addActionListener(new FireListener());
  
         //Create a panel that uses BoxLayout.
         JPanel buttonPane = new JPanel();
         buttonPane.setLayout(new BoxLayout(buttonPane,
                                            BoxLayout.LINE_AXIS));
         buttonPane.add(fireButton);
         buttonPane.add(Box.createHorizontalStrut(5));
         buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
         buttonPane.add(Box.createHorizontalStrut(5));
         buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
  
         add(buttonPane, BorderLayout.PAGE_END);
     }
  
     class FireListener implements ActionListener {
         public void actionPerformed(ActionEvent e) {
             testJavaToExecute.basicCallingExample();
         }
     }
  
     
     /**
      * Create the GUI and show it.  For thread safety,
      * this method should be invoked from the
      * event-dispatching thread.
      */
     private static void createAndShowGUI() {
         //Create and set up the window.
         JFrame frame = new JFrame("ListDemo");
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
         //Create and set up the content pane.
         JComponent newContentPane = new GUITest();
         newContentPane.setOpaque(true); //content panes must be opaque
         frame.setContentPane(newContentPane);
  
         //Display the window.
         frame.pack();
         frame.setVisible(true);
     }
  
     public static void main(String[] args) {
         //Schedule a job for the event-dispatching thread:
         //creating and showing this application's GUI.
         javax.swing.SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 createAndShowGUI();
             }
         });
     }

     @Override
     public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'valueChanged'");
     }
 }