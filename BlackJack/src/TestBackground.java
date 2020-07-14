import javax.swing.*;
import java.awt.*;

public class TestBackground extends JFrame {

	public TestBackground() 
	{
		JFrame frame = new JFrame("TestBackground");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setTitle("TestBackground");
        frame.setLayout(null);
		
		JLabel background;
		setSize(1440,900);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ImageIcon img = new ImageIcon("galaxy-wallpaper-2.jpg");
		
		background = new JLabel("Hello", img, JLabel.CENTER);
		background.setBounds(0,0,1440,900);
		add(background);
		
		setVisible(true);
	}
public static void main(String[] args) {
	new TestBackground();
}//main

}
