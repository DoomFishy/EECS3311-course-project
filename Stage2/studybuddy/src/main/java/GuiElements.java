
import javax.swing.*;

public class GuiElements {
    
    public void createButton(JFrame frame, String label, int xPos, int yPos, int width, int height) {
        JButton button = new JButton(label);
        button.setBounds(xPos, yPos, width, height);

        frame.add(button);
    }

    

}
