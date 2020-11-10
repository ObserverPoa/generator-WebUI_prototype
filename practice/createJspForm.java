import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class createJspForm {

	public static void main(String[] args) {
		if (args.length != 2)
		{
			System.out.println("usage: metadata_name output_name");
			return;
		}
		BufferedReader metadata = null;
		BufferedWriter jsp = null;
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
			jsp = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1], false), "UTF-8"));
			
			// store metadata in arraylist.
			String line;
			for (int i = 1; (line = metadata.readLine()) != null; ++i)
			{
				String[] field = line.trim().split("\\s*,\\s*"); // parse csv format
				if (field.length != 4) {
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
			
			jsp.write("<%@ page import=\"pangpang.service." + keyname + "Service\" %>\n");
			jsp.write("<%@ page import=\"pangpang.data.Dto" + keyname + "\" %>\n");
			jsp.write("\n");
			
			jsp.write("<%@ page language=\"java\" contentType=\"text/html; charset=utf-8\" pageEncoding=\"utf-8\" %>\n");
			jsp.write("<%\n");
			jsp.write("\trequest.setCharacterEncoding(\"utf-8\");\n\n");
			jsp.write("\tDto" + keyname + " dto = new(Dto" + keyname + ");\n");
			for (int i = 0; i < list.size(); ++i) { // write send input field to service code
				if (list.get(i).inout == Element.io.INPUT) {
					jsp.write("\tdto.set" + list.get(i).name + "(request.getParameter(\"a" + String.valueOf(i + 1) + "\"));\n");
				}
			}
			jsp.write("\n");
			jsp.write("\t" + keyname + "Service service = new(" + keyname + "Service);\n");
			jsp.write("\tservice.read(dto);\n");
			jsp.write("%>\n\n");
			
			jsp.write("<html>\n");
			jsp.write("<head>\n");
			jsp.write("</head>\n");
			
			jsp.write("<body>\n\n");
			
			jsp.write("<form");
			jsp.write(" action=\"" + "/action/" + keyname + "Form.jsp" + "\"");
			jsp.write(" method=\"" + "post" + "\"");
			jsp.write(">\n");
			jsp.write("  // Input fields\n");
			for (int i = 0; i < list.size(); ++i) { // write Input fields
				if (list.get(i).inout == Element.io.INPUT) {
					jsp.write("  <label");
					jsp.write(" for=\"a" + String.valueOf(i + 1) + "\"");
					jsp.write(">");
					jsp.write(list.get(i).label + ":");
					jsp.write("</label>\n");
					
					jsp.write("  <input");
					if (list.get(i).t == Element.type.TEXT) {
						jsp.write(" type=\"text\"");
					}
					else { // Element.type.NUMBER
						jsp.write(" type=\"number\"");
					}
					jsp.write(" id=\"a" + String.valueOf(i + 1) + "\"");
					jsp.write(" value=\"<%= dto.get" + keyname + "() %>\"");
					jsp.write(" />");
					jsp.write("<br>\n");
				}
			}
			jsp.write("  // Output fields\n");
			for (int i = 0; i < list.size(); ++i) { // write Input fields
				if (list.get(i).inout == Element.io.OUTPUT) {
					jsp.write("  <label");
					jsp.write(">");
					jsp.write(list.get(i).label + ":");
					jsp.write("</label>\n");
					
					jsp.write("  <%= dto.get" + list.get(i).name + "() %>");
					jsp.write("<br>\n");
				}
			}
			jsp.write("  <div class=\"button\">\n");
			jsp.write("    <button type=\"submit\">" + "Save" + "</button>\n");
			jsp.write("  </div>\n");
			jsp.write("</form>\n");
			
			jsp.write("\n</body>\n");
			jsp.write("</html>");
			
			jsp.flush();
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
			if (jsp != null)
			{
				try {
					jsp.close();
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
	enum io {
		INPUT, OUTPUT
	}
	
	String name;
	type t;
	String label;
	io inout;
	
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
		
		return 0;
	}
	
}

