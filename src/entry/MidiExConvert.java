package entry;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class MidiExConvert {
	public static void main(String[] arg){
		try{
			Scanner cat = new Scanner(new File("bkup/ExCategory.txt"));
			String line;
			while(cat.hasNextLine()){
				line=cat.nextLine().trim();
				Scanner message = new Scanner(new File("bkup/Ex"+line+"Message.txt"));
				Scanner name = new Scanner(new File("bkup/Ex"+line+"Name.txt"));
				FileWriter fw = new FileWriter("Ex"+line+".txt");
				while(true){
					if(message.hasNextLine() && name.hasNextLine()){
						fw.write(message.nextLine().trim()+" "+name.nextLine().trim()+"\n");
					}else if(message.hasNextLine()!=name.hasNextLine()){
						throw new Exception();
					}else{
						break;
					}
				}
				message.close();
				name.close();
				fw.close();
			}
			cat.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
