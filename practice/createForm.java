import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class createForm
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.out.println("usage: metadata_name output_name");
			return;
		}
		BufferedReader metadata = null;
		BufferedWriter html = null;
		
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
			html = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1], false), "UTF-8"));
			
			html.write("<!DOCTYPE html>\n"); // docuement type declaration
			html.write("<html>\n");
			html.write("<head>\n");
			html.write("</head>\n");
			
			html.write("<body>\n\n");
			
			html.write("<form");
			html.write(" action=\"" + "/action/" + keyname + "Servlet" + "\"");
			html.write(" method=\"" + "post" + "\"");
			html.write(">\n");
			
			String line;
			for (int i = 1; (line = metadata.readLine()) != null; ++i)
			{
				String[] field = line.trim().split("\\s*,\\s*"); // csv format
				if (field.length != 3) {
					System.out.println("field count is not 3 at line " + i);
					continue;
				}
				
				String type;
				if (field[1].equals("Text")) {
					type = "text";
				}
				else if (field[1].equals("Number")) {
					type = "number";
				}
				else {
					System.out.println("unknown data type at line " + i);
					continue;
				}
				
				String id = "a" + String.valueOf(i);
				
				html.write("  <label");
				html.write(" for=\"" + id + "\"");
				html.write(">");
				html.write(field[2] + ":");
				html.write("</label>\n");
				
				html.write("  <input");
				html.write(" type=\"" + type + "\"");
				html.write(" id=\"" + id + "\"");
				html.write(" />");
				html.write("<br>\n");
				
				html.flush();
			}
			html.write("\n");
			
			html.write("  <div class=\"button\">\n");
			html.write("    <button type=\"submit\">" + "Save" + "</button>\n");
			html.write("  </div>\n");
			
			html.write("</form>\n");
			
			html.write("\n</body>\n");
			html.write("</html>");
			
			html.flush();
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
			if (html != null)
			{
				try {
					html.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return;
	}
}
