package entry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.sound.midi.*;

public class MidiRecord {
	public static void main(String[] args) {
		(new MidiRecord()).run();
	}

	private JButton reserveButton;
	private JButton releaseButton;
	private JButton refreshButton;
	private JComboBox selectDevice;
	private List<MidiDevice.Info> infos;
	private MidiDevice currentDevice;
	private MyReceiver myReceiver;
	private JTextArea outputArea;

	class MyReceiver implements Receiver{
		@Override
		public void send(MidiMessage message, long timeStamp) {
	        if (message instanceof ShortMessage) {
	        	ShortMessage sm = ((ShortMessage)message);
	            switch(sm.getCommand()) {
	                case ShortMessage.NOTE_ON:
	                	if(sm.getData2()!=0x00){
		                	output(message.getMessage(),"ノートオン");
	                	}else{
		                	output(message.getMessage(),"ノートオフ");
	                	}
	                    break;
	                case ShortMessage.NOTE_OFF:
	                	output(message.getMessage(),"ノートオフ");
	                    break;
	                case ShortMessage.CHANNEL_PRESSURE:
	                case ShortMessage.POLY_PRESSURE:
	                	output(message.getMessage(),"アフタータッチ");
	                	break;
	                case ShortMessage.CONTROL_CHANGE:
	                	if(sm.getData2()==0x0b){
		                	output(message.getMessage(),"エクスプレッション(ExpPedal)");
	                	}else{
		                	output(message.getMessage(),"コントロールチェンジ");
	                	}
	                	break;
	                case ShortMessage.PROGRAM_CHANGE:
	                	output(message.getMessage(),"プログラムチェンジ(レジストチェンジ)");
	                	break;
	                case ShortMessage.PITCH_BEND:
	                	output(message.getMessage(),"ピッチベンド(2ndExpPedal)");
	                	break;

	                case ShortMessage.START:
	                	output(message.getMessage(),"リズムスタート");
	                	break;
	                case ShortMessage.STOP:
	                	output(message.getMessage(),"リズムストップ");
	                	break;

	                case ShortMessage.TIMING_CLOCK:
	                	output(message.getMessage(),"クロック");
	                	break;
	                case ShortMessage.ACTIVE_SENSING:
	                	output(message.getMessage(),"アクティブセンシング");
	                	break;

                    default:
	                	output(message.getMessage(),"その他");
                    	break;
	            }
	        }
	        if (message instanceof MetaMessage) {
            	output(message.getMessage(),"メタイベント");
	        }
	        if (message instanceof SysexMessage) {
            	output(message.getMessage(),"システムエクスクルーシブ");
            	onExclusive(message.getMessage());
	        }
		}
		public void onExclusive(byte[] b){
			byte first[]={(byte)0xf0,(byte)0x43,(byte)0x70,(byte)0x78,(byte)0x44,(byte)0x10,(byte)0x00};
			byte last[]={(byte)0xf7};
			boolean flag=true;
			try{
				for(int i=0;i<first.length && flag;i++){
					if(first[i]!=b[i])flag=false;
				}
				for(int i=0;i<last.length && flag;i++){
					if(last[last.length-1-i]!=b[b.length-1-i])flag=false;
				}
				if(flag){
					outputString="";
					for(int i=first.length;i+last.length<b.length;i++){
		        		String str = Integer.toHexString(b[i]&0xff);
		        		if(str.length()==1){
		        			str="0"+str;
		        		}
		        		outputString+=str.toUpperCase()+" ";
					}
					try {
						SwingUtilities.invokeAndWait(
							new Runnable(){
								@Override
								public void run(){
						            outputArea.append(outputString+"\n");
						            outputArea.setCaretPosition(outputArea.getText().length());
								}
							}
						);
					} catch (InvocationTargetException e) {
					} catch (InterruptedException e) {
					}
					FileWriter pw = new FileWriter("ExMessage.txt",true);
					pw.write(outputString+" : \n");
					pw.close();
				}
			}catch(Exception e){
			}
		}
		public String outputString;
		public void output(byte[] status,String exp){
			if(status[0] == (byte)0xf0)return;
			if(status[0] == (byte)0xf8)return;
			if(status[0] == (byte)0xfe)return;
			outputString="";
        	for(byte b : status){
        		String str = Integer.toHexString(b&0xff);
        		if(str.length()==1){
        			str="0"+str;
        		}
        		outputString+=str.toUpperCase()+" ";
        	}
        	if(outputString.length()<12){
        		outputString=String.format("%12s",outputString);
        	}
        	outputString+=": "+exp+"\n";
			try {
				SwingUtilities.invokeAndWait(
					new Runnable(){
						@Override
						public void run(){
				            outputArea.append(outputString);
				            outputArea.setCaretPosition(outputArea.getText().length());
						}
					}
				);
			} catch (InvocationTargetException e) {
			} catch (InterruptedException e) {
			}
		}
		@Override
		public void close() {
		}
	}
	public void run(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				reserveButton = new JButton("Reserve");
				releaseButton = new JButton("Release");
				refreshButton = new JButton("Refresh");

				selectDevice = new JComboBox();
				infos = new ArrayList<MidiDevice.Info>();
				myReceiver = new MyReceiver();
				outputArea = new JTextArea(10,80);

				releaseButton.setEnabled(false);

				outputArea.setBorder(new EtchedBorder(EtchedBorder.RAISED));
				outputArea.setEditable(false);
				outputArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));

				reserveButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if(reserveDevice()){
							reserveButton.setEnabled(false);
							releaseButton.setEnabled(true);
							refreshButton.setEnabled(false);
							selectDevice.setEnabled(false);
						}
					}
				});
				releaseButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						releaseDevice();
						reserveButton.setEnabled(true);
						releaseButton.setEnabled(false);
						refreshButton.setEnabled(true);
						selectDevice.setEnabled(true);
					}
				});
				refreshButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						refreshDeviceList();
					}
				});

				refreshDeviceList();

				panel.add(reserveButton);
				panel.add(refreshButton);
				panel.add(releaseButton);
				panel.add(selectDevice);

				JScrollPane sc = new JScrollPane(outputArea);
				sc.setPreferredSize(new Dimension(600,400));
				panel.add(sc);

				frame.setTitle("Midi Receiver");
				frame.setBounds(100,100,640,500);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(panel,BorderLayout.CENTER);
				frame.setVisible(true);
//				frame.pack();
			}
		});
	}
	public boolean reserveDevice(){
		try {
			outputArea.setText("");
			currentDevice = MidiSystem.getMidiDevice(infos.get(selectDevice.getSelectedIndex()));
			if(currentDevice.isOpen())throw new MidiUnavailableException();
			currentDevice.open();
			Transmitter transmitter = currentDevice.getTransmitter();
			transmitter.setReceiver(myReceiver);
			return true;
		} catch (MidiUnavailableException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean releaseDevice(){
		try {
			if(!currentDevice.isOpen())throw new MidiUnavailableException();
			currentDevice.close();
			return true;
		} catch (MidiUnavailableException e) {
			return false;
		}
	}
	public void refreshDeviceList(){
		selectDevice.removeAllItems();
		infos.clear();
		MidiDevice.Info[] allinfos = MidiSystem.getMidiDeviceInfo();
		for(MidiDevice.Info info:allinfos){
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);
	            if (!(device instanceof Sequencer) && !(device instanceof Synthesizer)) {
	            	if(device.getMaxTransmitters()!=0){
	            		selectDevice.addItem(info);
	            		infos.add(info);
	            	}
				}
			} catch (MidiUnavailableException e) {
			}
		}
	}
}
