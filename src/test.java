import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import info.clearthought.layout.TableLayout;

public class test {
	JFrame f;

	test() {
		f = new JFrame();
		double size[][] = { { 180, 60, 90, 60, 180, 190 }, {} };
		f.setLayout(new TableLayout(size));
		JTextArea ta = new JTextArea(500, 500);
		JPanel p1 = new JPanel();
		p1.add(ta);
//		p1.setPreferredSize(new Dimension(200, 3000));
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JTabbedPane tp = new JTabbedPane();
		tp.setBounds(50, 50, 200, 200);
		tp.add("main", p1);
		tp.add("visit", p2);
		tp.add("help", p3);
		tp.setSize(new Dimension(2000, 3000));
		f.add(tp);
		f.setSize(4000, 4000);
		f.setLayout(null);
		f.setVisible(true);
		
	}

	public static void main(String[] args) {
		new test();
	}
}