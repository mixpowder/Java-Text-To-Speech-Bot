package mixpowder.jttsbot.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SetFrame extends JFrame{

	public SetFrame(String title,String message,int h,int w){
		JLabel label = new JLabel(message);

		setTitle(title);

		add(label);

		setBounds(250,250,h,w);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

}
