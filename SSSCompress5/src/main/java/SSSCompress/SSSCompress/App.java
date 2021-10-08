package SSSCompress.SSSCompress;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class App {
	private JFrame frame;
	private JTextField txtFileIn;
	private JTextField txtFileOut;
	private String strFileIn;
	private String strFileOut;
	static StringBuilder soilAsBinary = new StringBuilder("");
	static StringBuilder compressedAsBinary = new StringBuilder("");
	private static int soilBase = 256;
	private static int currentPower = 4;
	private static long currentBase = 0;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public App() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txtFileIn = new JTextField();
		txtFileIn.setText("Update.mp4");
		txtFileIn.setEditable(false);
		txtFileIn.setBounds(10, 56, 110, 20);
		frame.getContentPane().add(txtFileIn);
		txtFileIn.setColumns(10);

		JLabel lblFileIn = new JLabel("File to Convert:");
		lblFileIn.setBounds(10, 39, 110, 14);
		frame.getContentPane().add(lblFileIn);

		JLabel lblFileOut = new JLabel("Output File Name:");
		lblFileOut.setBounds(10, 106, 110, 14);
		frame.getContentPane().add(lblFileOut);

		JLabel lblWarning = new JLabel("");
		lblWarning.setBounds(10, 11, 414, 17);
		frame.getContentPane().add(lblWarning);

		txtFileOut = new JTextField();
		txtFileOut.setText("output");
		txtFileOut.setColumns(10);
		txtFileOut.setBounds(10, 120, 110, 20);
		frame.getContentPane().add(txtFileOut);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
				dialog.setMode(FileDialog.LOAD);
				dialog.setVisible(true);
				strFileIn = dialog.getFile();
				txtFileIn.setText(strFileIn);
				System.out.println(strFileIn + " chosen.");
			}
		});
		btnBrowse.setBounds(130, 55, 89, 23);
		frame.getContentPane().add(btnBrowse);

		JButton btnConvert = new JButton("Convert");
		btnConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!txtFileIn.getText().isEmpty()) {
					if (!txtFileOut.getText().isEmpty()) {
						strFileOut = txtFileOut.getText() + ".bin";
						try (InputStream inputStream = new FileInputStream(strFileOut);) {
							long Max_Value = Long.parseLong(Integer.toString(Integer.MAX_VALUE));
							long maxStorable = (Max_Value) * (Max_Value);
							for (int i = 1; i <= currentPower; i++) {
								if (maxStorable >= currentBase) {
									currentBase = (long) Math.pow(soilBase, i);
									System.out.println(currentBase);
								} else {
									System.out.println(i - 1);
									break;
								}
							}

							System.out.print("Calculating soil values ");
							System.out.println("smaller than or equal to " + currentBase);

							BuildSoil.soilBuilder(currentPower);
							inputStream.close();

							System.out.println("End");
							System.exit(0);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}

					else {
						lblWarning.setText("Output File Name is required.");
					}
				} else

				{
					lblWarning.setText("File to Convert is required.");
				}
			}
		});
		btnConvert.setBounds(165, 228, 89, 23);
		frame.getContentPane().add(btnConvert);
	}

	public static void fileDelimiter(File fileIn, BigInteger bi) throws IOException {
		Scanner sc = null;
		StringBuilder currentFactors = new StringBuilder();
		try {
			sc = new Scanner(fileIn);
			sc.useDelimiter(";");
			
			while (sc.hasNextLine()) {
				currentFactors.append(sc.next() + ";");
			}
		} catch (IOException exp) {
			exp.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}
	}
}