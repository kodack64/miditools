package entry;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class MidiAddSysEx {
	public static void main(String[] args) {
		(new MidiAddSysEx()).run();
	}

	public JFrame frame;
	public JPanel panel;
	public JTextField readFileName;
	public JTextField saveFileName;
	public JButton readFileChoose;
	public JButton saveFileChoose;
	public JButton processButton;
	public File readFile;
	public File saveFile;
	public JComboBox selectType;

	public void convert(){
		try {
			if(readFile==null || saveFile==null){
				return;
			}
			int currentType = MidiSystem.getMidiFileFormat(readFile).getType();
			if(currentType==1){
				Sequence seq=MidiSystem.getSequence(readFile);
//				if(seq.getTracks().length==0)seq.createTrack();
//				Track track=seq.getTracks()[0];
//				SysexMessage sysexMessage = new SysexMessage();
//				byte[] message = {(byte)0xf0,(byte)0xf0,(byte)0xf0,(byte)0xf0,(byte)0xf0,(byte)0xf0,(byte)0xf0,(byte)0xf7};
//				sysexMessage.setMessage(message,message.length);
//				track.add(new MidiEvent(sysexMessage,0));
//				MidiSystem.getSequencer().setSequence(seq);
				int formatnum = Integer.parseInt(selectType.getSelectedItem().toString());
				MidiSystem.write(seq,formatnum,saveFile);
			}else{
				Sequence seq=MidiSystem.getSequence(readFile);
				int formatnum = Integer.parseInt(selectType.getSelectedItem().toString());
				MidiSystem.write(seq,formatnum,saveFile);
			}

		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (MidiUnavailableException e) {
//			e.printStackTrace();
		} catch (Exception e){
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
				readFileChoose = new JButton("Select Read MidiFile");
				saveFileName=new JTextField(20);
				saveFileChoose = new JButton("Select Save MidiFile");
				processButton=new JButton("Process");

				selectType = new JComboBox();
				for(int type:MidiSystem.getMidiFileTypes()){
					selectType.addItem(type);
				}

				readFileChoose.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser filechooser = new JFileChooser();
						filechooser.addChoosableFileFilter(new FileFilter(){
							@Override
							public boolean accept(File arg0) {
								return arg0.isDirectory() || (arg0.isFile() && arg0.getName().endsWith(".mid"));
							}
							@Override
							public String getDescription() {
								return "Midi File(.mid)";
							}
						});
						int selected = filechooser.showOpenDialog(frame);
						if (selected == JFileChooser.APPROVE_OPTION){
							readFile = filechooser.getSelectedFile();
							readFileName.setText(readFile.toString());
						}
					}
				});
				saveFileChoose.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser filechooser = new JFileChooser();
						filechooser.addChoosableFileFilter(new FileFilter(){
							@Override
							public boolean accept(File arg0) {
								return true;
							}
							@Override
							public String getDescription() {
								return "Midi File(.mid)";
							}
						});
						int selected = filechooser.showSaveDialog(frame);
						if (selected == JFileChooser.APPROVE_OPTION){
							saveFile = filechooser.getSelectedFile();
							saveFileName.setText(saveFile.toString());
						}
					}
				});
				processButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						convert();
					}
				});

				readFileName.setEnabled(false);

				panel.add(readFileChoose);
				panel.add(readFileName);
				panel.add(saveFileChoose);
				panel.add(saveFileName);
				panel.add(selectType);
				panel.add(processButton);

				frame.setTitle("Midi Receiver");
				frame.setBounds(100,100,320,320);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(panel,BorderLayout.CENTER);
				frame.setVisible(true);
			}
		});
	}
}
