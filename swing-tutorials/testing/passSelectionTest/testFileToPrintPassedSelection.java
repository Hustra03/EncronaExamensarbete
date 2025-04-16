import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;

public class testFileToPrintPassedSelection {
    
    public static void basicSelectionPassingExample(ListSelectionModel listModel)
    {

        System.out.println("This was called by the GUI and will print the currently selected items in the list now:");
        System.out.println(listModel.getMaxSelectionIndex());
        System.out.println(listModel.getMinSelectionIndex());

        for (int i = 0; i < listModel.getSelectedIndices().length; i++) {
            System.out.println(i);
        }
    }


}
