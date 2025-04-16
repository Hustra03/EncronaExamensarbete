import javax.swing.DefaultListModel;

public class testFileToPrintPassedData {
    
    public static void basicDataPassingExample(DefaultListModel<String> listModel)
    {


        System.out.println("This was called by the GUI and will print the current list model now:");
        
        for (int i = 0; i < listModel.size(); i++) {
            System.out.println(i+" " + listModel.get(i));
        }
    }


}
