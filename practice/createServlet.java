import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class createServlet 
{
	public static void main(String[] args) 
	{
		if (args.length != 2)
		{
			System.out.println("usage: metadata_name output_name");
			return;
		}
		BufferedReader metadata = null;
		BufferedWriter servlet = null;
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
			servlet = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1], false), "UTF-8"));
			
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
			
			servlet.write("import Dto" + keyname + ";\n");
			servlet.write("import " + keyname + "Service;\n"); // TODO: rule of Service class name from metadata name is required.
			servlet.write("\n");
			
			servlet.write("public class " + keyname + "Servlet extends HttpServlet {\n");
			
			servlet.write("\tprotected void doPost(\n");
			servlet.write("\t\tHttpServletRequest request,\n");
			servlet.write("\t\tHttpServletResponse response) throws ServletExpection {\n\n");
			
			servlet.write("\t\tDto" + keyname + " dto = new Dto" + keyname + ";\n\n");
			
			// write assigning form to dto code
			for (int i = 0; i < list.size(); ++i) {
				servlet.write("\t\tdto." + list.get(i).name + " = request.getParameter(\"a" + String.valueOf(i + 1) + "\");\n");
			}
			servlet.write("\n");
			
			servlet.write("\t\t" + keyname + "Service service = new " + keyname + "Service;\n\n");
			servlet.write("\t\tint ret = service.save(dto);\n\n");
			servlet.write("\t\tPrintWriter writer = response.getWriter();\n\n");
			
			servlet.write("\t\tString htmlResponse = \"<html>\";\n");
			servlet.write("\t\tif (ret == 0) {\n");
			servlet.write("\t\t\thtmlResponse += \"<h2>Save service executed successfully.\";\n");
			servlet.write("\t\t}\n");
			servlet.write("\t\telse {\n");
			servlet.write("\t\t\thtmlResponse += \"<h2>Error occurred with code :\" + String.valueOf(ret);\n");
			servlet.write("\t\t}\n\n");
			
			servlet.write("\t\twriter.println(htmlResponse);\n");
			
			servlet.write("\t}\n");
			
			servlet.write("}");
			
			servlet.flush();
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
			if (servlet != null)
			{
				try {
					servlet.close();
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
