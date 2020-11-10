import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.lang.NumberFormatException;

public class createJsForm {

	public static void main(String[] args) {
		if (args.length != 2)
		{
			System.out.println("usage: metadata_name output_name");
			return;
		}
		BufferedReader metadata = null;
		BufferedWriter js = null;
		ArrayList<Element> list = new ArrayList<Element>();
		
		/* get filenane excluding extension */
		String keyname1 = getKeyname(args[0]);
		String keyname2 = getKeyname(args[1]);
		
		try
		{
			metadata = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			js = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1], false), "UTF-8"));
			
			// store metadata in arraylist.
			String line;
			for (int i = 1; (line = metadata.readLine()) != null; ++i)
			{
				String[] field = line.trim().split("\\s*,\\s*", -1); // parse csv format
				if (field.length != 7) {
					System.out.println("field count is not 3 at line " + i);
					continue;
				}
				
				Element e = new Element();
				if (e.set(field) == 0) {
					list.add(e);
				}
				else {
					System.out.println("unknown data at line " + i);
				}
			}
			
			js.write("function " + keyname2 + "() {\n");
			boolean startedif = false;
			for (int i = 0; i < list.size(); ++i) { // write send input field to service code
				if (list.get(i).required) {
					if (!startedif)
					{
						js.write("\tif ");
						startedif = true;
					}
					else {
						js.write("\telse if ");
					}
					js.write("(saveForm.a" + String.valueOf(i + 1) + ".value == \"\") {\n");
					js.write("\t\talert(\"상품번호를 입력해 주세요.\");\n");
					js.write("\t\tsaveForm.a" +  String.valueOf(i + 1) + ".focus();\n");
					js.write("\t\treturn false;\n");
					js.write("\t}\n");
				}
				if (list.get(i).min != null) {
					if (!startedif)
					{
						js.write("\tif ");
						startedif = true;
					}
					else {
						js.write("\telse if ");
					}
					js.write("(saveForm.a" + String.valueOf(i + 1) + ".value < " + String.valueOf(list.get(i).min) + ") {\n");
					js.write("\t\talert(\"크기가 " + String.valueOf(list.get(i).min) + " 이상이여야 합니다.\");\n");
					js.write("\t\tsaveForm.a" +  String.valueOf(i + 1) + ".focus();\n");
					js.write("\t\treturn false;\n");
					js.write("\t}\n");
				}
				if (list.get(i).max != null) {
					if (!startedif)
					{
						js.write("\tif ");
						startedif = true;
					}
					else {
						js.write("\telse if ");
					}
					js.write("(saveForm.a" + String.valueOf(i + 1) + ".value > " + String.valueOf(list.get(i).max) + ") {\n");
					js.write("\t\talert(\"크기가 " + String.valueOf(list.get(i).max) + " 이하이여야 합니다.\");\n");
					js.write("\t\tsaveForm.a" +  String.valueOf(i + 1) + ".focus();\n");
					js.write("\t\treturn false;\n");
					js.write("\t}\n");
				}
			}
			js.write("\telse\n");
			js.write("\t\treturn true;\n");
			js.write("}");
			
			js.flush();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if (metadata != null)
			{
				try {
					metadata.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (js != null)
			{
				try {
					js.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return;

	}
	
	public static String getKeyname(String arg) 
	{
		if (arg.contains(".") && arg.length() > 1)
		{
			int pos = arg.lastIndexOf("."); // last dot pos
			if (pos == 0) { 
				return arg.substring(1); // if only leading dot exist in input file name
			}
			else {
				return arg.substring(0, pos);
			}
		}
		else {
			return arg;
		}
	}

}

class Element
{
	enum type {
		TEXT, NUMBER
	}
	enum io {
		INPUT, OUTPUT
	}
	
	String name;
	type t;
	String label;
	io inout;
	boolean required;
	Integer min, max;
	
	public int set(String[] field)
	{
		this.name = field[0];
		
		if (field[1].equals("Text")) {
			t = type.TEXT;
		}
		else if (field[1].equals("Number")) {
			t = type.NUMBER;
		}
		else {
			return -1;
		}
		
		this.label = field[2];
		
		if (field[3].equals("Input")) {
			this.inout = io.INPUT;
		}
		else if (field[3].equals("Output")) {
			this.inout = io.OUTPUT;
		}
		else {
			return -1;
		}
		
		if (field[4].equals("True")) {
			this.required = true;
		}
		else if (field[4].equals("False") || field[4].equals("")) {
			this.required = false;
		}
		else {
			return -1;
		}
		
		try
		{
			this.min = field[5].equals("") ? null : Integer.parseInt(field[5]);
			this.max = field[6].equals("") ? null : Integer.parseInt(field[6]);
		}
		catch (NumberFormatException e) {
			return -1;
		}
		
		return 0;
	}
	
}
