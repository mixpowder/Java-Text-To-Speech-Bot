package mixpowder.jttsbot.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ErrorFrame extends JFrame{

	public ErrorFrame(String message){
		JLabel label = new JLabel(message);

		setTitle("エラー");

		add(label);

		setBounds(250,250,495,100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

}
