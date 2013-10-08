/**
 * バルクからスタイルを切り出して保存
 */


package entry;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class Bulk2Style {
	public static void main(String[] args) {
		(new Bulk2Style()).run();
	}

	public JFrame frame;
	public JPanel panel;
	public JTextField readFileName;
	public JButton readFileChoose;
	public JButton processButton;
	public JButton processButton2;
	public JComboBox selectUserRhythm;
	public File readFile;
	public File saveFile;
	public String strext;

	public void convert(){
		try {
			if(readFile==null || readFileName==null){
				return;
			}
			int midicount=0,selected=selectUserRhythm.getSelectedIndex()+1;
			int[] b = {0,0,0,0};
			int[] st = {0x4D,0x54,0x68,0x64};
			FileInputStream input = new FileInputStream(readFile);
			String fullpath = readFile.getPath();
			String filename = readFile.getName();
			String outputDirectory = fullpath.substring(0,fullpath.length()-filename.length());
			String outputName = outputDirectory + filename.substring(0,filename.length()-4)+"_User_"+selected+strext;
			FileOutputStream output = new FileOutputStream(outputName);
			while(midicount<=selected){
				if(midicount==selected){
					output.write(b[0]);
				}
				for(int q=0;q+1<b.length;q++){
					b[q]=b[q+1];
				}
				b[b.length-1]=input.read();
				if(b[b.length-1]==-1)break;
				boolean flag=true;
				for(int q=0;q<b.length && flag;q++){
					if(b[q]!=st[q]){
						flag=false;
					}
				}
				if(flag){
					midicount++;
				}
			}
			input.close();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				frame = new JFrame();
				panel = new JPanel();

				readFileName=new JTextField(20);
				readFileChoose = new JButton("Select ELS Bulk Data File (.Bxx)");

				selectUserRhythm = new JComboBox();
				for(int num = 1 ; num<=48 ; num++){
					selectUserRhythm.addItem(num);
				}

				processButton=new JButton(".Bxx -> .sty");
				processButton2=new JButton(".Bxx -> .mid(type0)");

				readFileChoose.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser filechooser = new JFileChooser();
						filechooser.addChoosableFileFilter(new FileFilter(){
							@Override
							public boolean accept(File arg0) {
								if(arg0.isDirectory())return true;
								if(!arg0.isFile())return false;
								if(arg0.length()<5)return false;
								String name = arg0.getName();
								if(
										(('0'<=name.charAt(name.length()-1)) && (name.charAt(name.length()-1)<='9') ) &&
										(('0'<=name.charAt(name.length()-2)) && (name.charAt(name.length()-2)<='9') ) &&
										((name.charAt(name.length()-3) == 'B') || (name.charAt(name.length()-3) == 'b') ) &&
										(name.charAt(name.length()-4)=='.')
										){
									return true;
								}else{
									return false;
								}
							}
							@Override
							public String getDescription() {
								return "SongData File(.Bxx)";
							}
						});
						int selected = filechooser.showOpenDialog(frame);
						if (selected == JFileChooser.APPROVE_OPTION){
							readFile = filechooser.getSelectedFile();
							readFileName.setText(readFile.toString());
						}
					}
				});
				processButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						strext=".sty";
						convert();
					}
				});
				processButton2.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						strext=".mid";
						convert();
					}
				});

				readFileName.setEditable(false);

				panel.add(readFileChoose);
				panel.add(readFileName);
				panel.add(new JLabel("UserRhythm"));
				panel.add(selectUserRhythm);
				panel.add(processButton);
				panel.add(processButton2);

				frame.setTitle("Regist2Style");
				frame.setBounds(100,100,320,320);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(panel,BorderLayout.CENTER);
				frame.setVisible(true);
			}
		});
	}
}
