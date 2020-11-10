import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class createDto 
{
	public static void main(String[] args) 
	{
		if (args.length != 2)
		{
			System.out.println("usage: metadata_name output_name");
			return;
		}
		BufferedReader metadata = null;
		BufferedWriter dto = null;
		ArrayList<Element> list = new ArrayList<Element>();
		
		/* get filenane excluding extension */
		String keyname;
		if (args[0].contains(".") && args[0].length() > 1)
		{
			int pos = args[0].lastIndexOf("."); // last dot pos
			if (pos == 0) { 
				keyname = args[0].substring(1); // if only leading dot exist in input file name
			}
			else {
				keyname = args[0].substring(0, pos);
			}
		}
		else {
			keyname = args[0];
		}
		
		try
		{
			metadata = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			dto = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1], false), "UTF-8"));
			
			// store metadata in arraylist.
			String line;
			for (int i = 1; (line = metadata.readLine()) != null; ++i)
			{
				String[] field = line.trim().split("\\s*,\\s*"); // parse csv format
				if (field.length != 3) {
					System.out.println("field count is not 3 at line " + i);
					continue;
				}
				
				if (field[1].equals("Text")) {
					list.add(new Element(field[0], Element.type.TEXT, field[2]));
				}
				else if (field[1].equals("Number")) {
					list.add(new Element(field[0], Element.type.NUMBER, field[2]));
				}
				else {
					System.out.println("unknown data type at line " + i);
				}
			}
			
			dto.write("public class " + "Dto" + keyname + " {\n");
			
			// write DTO class private variables
			for (int i = 0; i < list.size(); ++i)
			{
				dto.write("\tprivate ");
				if (list.get(i).t == Element.type.TEXT) {
					dto.write("String ");
				}
				else if (list.get(i).t == Element.type.NUMBER) {
					dto.write("int ");
				}
				else {
					System.out.println("enum error!");
				}
				dto.write(list.get(i).name + ";\n");
			}
			dto.write("\n");
			
			// write DTO class getters and setters
			for (int i = 0; i < list.size(); ++i)
			{
				String name = list.get(i).name;
				String type = "";
				if (list.get(i).t == Element.type.TEXT) {
					type = "String ";
				}
				else if (list.get(i).t == Element.type.NUMBER) {
					type = "int ";
				}
				else {
					System.out.println("enum error!");
				}
				
				dto.write("\tpublic " + type + "get" + name + "() {\n");
				dto.write("\t\treturn " + name + ";\n");
				dto.write("\t}\n\n");
				
				dto.write("\tpublic void set" + name + "(" + type + name + ") {\n");
				dto.write("\t\tthis." + name + " = " + name + ";\n");
				dto.write("\t}\n\n");
			}
			
			dto.write("}");
			
			dto.flush();
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
			if (dto != null)
			{
				try {
					dto.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return;

	}

}

class Element
{
	enum type {
		TEXT, NUMBER
	}
	
	String name;
	type t;
	String label;
	
	public Element(String name, type t, String label)
	{
		this.name = name;
		this.t = t;
		this.label = label;
	}
	
}
