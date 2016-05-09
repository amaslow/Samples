package samples;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class ContextMenuMouseListener extends MouseAdapter {
    private JPopupMenu popup = new JPopupMenu();

    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action undoAction;
    private Action selectAllAction;
    private Action reddenAction;
        
    private JTextComponent textComponent;
    private String savedString = "";
    private JTextPane textPane;
    
    private Actions lastActionSelected;

    private enum Actions { UNDO, CUT, COPY, PASTE, SELECT_ALL, REDDEN };

    public ContextMenuMouseListener() {
        undoAction = new AbstractAction("Undo") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                    textComponent.setText("");
                    textComponent.replaceSelection(savedString);
               

                    lastActionSelected = Actions.UNDO;
            }
        };

        popup.add(undoAction);
        popup.addSeparator();

        cutAction = new AbstractAction("Cut") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.CUT;
                savedString = textComponent.getText();
                textComponent.cut();
               
            }
        };

        popup.add(cutAction);

        copyAction = new AbstractAction("Copy") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.COPY;
                textComponent.copy();
            }
        };

        popup.add(copyAction);

        pasteAction = new AbstractAction("Paste") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.PASTE;
                savedString = textComponent.getText();
                textComponent.paste();
            }
        };

        popup.add(pasteAction);
        popup.addSeparator();

        selectAllAction = new AbstractAction("Select All") 
        {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.SELECT_ALL;
                textComponent.selectAll();
            }
        };

        popup.add(selectAllAction);
        popup.addSeparator();
        
        reddenAction = new AbstractAction("Redden") 
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                lastActionSelected = Actions.REDDEN;
                StyledDocument doc = textPane.getStyledDocument();
              
                int start = textPane.getSelectionStart();
                int end = textPane.getSelectionEnd();       
                 if (start == end) 
                    { // No selection, cursor position.
                    return;
                    }
                 if (start > end)
                    { // Backwards selection?
                    int life = start;
                    start = end;
                    end = life;
                    }
                 Style style = textPane.addStyle("MyHilite", null);
                 StyleConstants.setForeground(style, Color.RED.darker());
                 doc.setCharacterAttributes(start, end - start, style, false);
                 
            }
        };

        popup.add(reddenAction);
   
        
    }

    @Override
    public void mouseClicked(MouseEvent e) 
    {
        if (e.getModifiers() == InputEvent.BUTTON3_MASK) 
        {
            if (!(e.getSource() instanceof JTextComponent))
            {
                
                return;
            }

            textComponent = (JTextComponent) e.getSource();
            textComponent.requestFocus();

            textPane = (JTextPane) e.getSource();
            textPane.requestFocus();
            
            boolean enabled = textComponent.isEnabled() || textPane.isEditable();
            boolean editable = textComponent.isEditable();
            boolean nonempty = !(textComponent.getText() == null || textComponent.getText().equals(""));
            boolean marked = textComponent.getSelectedText() != null || textPane.getSelectedText() !=null;
            
                               
            boolean pasteAvailable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).isDataFlavorSupported(DataFlavor.stringFlavor);

            undoAction.setEnabled(enabled && editable && (lastActionSelected == Actions.CUT || lastActionSelected == Actions.PASTE));
            cutAction.setEnabled(enabled && editable && marked);
            copyAction.setEnabled(enabled && marked);
            pasteAction.setEnabled(enabled && editable && pasteAvailable);
            selectAllAction.setEnabled(enabled && nonempty);
            reddenAction.setEnabled(enabled && marked);

            int nx = e.getX();

            if (nx > 500) {
                nx = nx - popup.getSize().width;
            }

            popup.show(e.getComponent(), nx, e.getY() - popup.getSize().height);
        }
    }
}