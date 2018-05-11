package com.langu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;


public class ProtoReader {
	
	private BufferedReader input;

	public ProtoReader() {
		try {
			Properties prop = new Properties();
			
			InputStream is = ProtoReader.class.getResourceAsStream("/config.properties");
			prop.load(new InputStreamReader(is, "utf8")); 	is.close();
			
			String PROTO_FILE_PATH = prop.getProperty("PROTO_FILE_PATH");
			String EXPORT_LUA_NAME = prop.getProperty("EXPORT_LUA_NAME");
			String EXPORT_LUA_PATH = prop.getProperty("EXPORT_LUA_PATH");
			
			File file = new File(PROTO_FILE_PATH);
			
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".proto");
				}
			};
			
			String definestr = "";
			
			//导出proto定义
			for(File proto : file.listFiles(filter)) {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(proto), "utf8");
				
				input = new BufferedReader(reader);
				
				String filename = proto.getName().replaceAll(".proto", "") + "_pb";
				
				String last = "";
				String line = input.readLine();
				while(line != null) {
					if(line.contains("message") && line.contains("_")) {
						line = line.replaceAll("message\\s", "");	line = line.replaceAll("\\{", "").trim();
						
						String newmsg = String.format("%s\n%s[%s] = {classname=\"%s\", constructor=\"%s\"}\n\n",
								last.replace("//", "--"), EXPORT_LUA_NAME, line.substring(line.indexOf("_") + 1).trim(), filename, line);
						
						definestr += newmsg;
					}
					
					last = line;	line = input.readLine();
				}
				
				input.close();
				reader.close();
			}
			
			definestr = String.format("%s = {};\n\n%s", EXPORT_LUA_NAME, definestr);
			
			String filepath = String.format("%s\\%s.lua", EXPORT_LUA_PATH, EXPORT_LUA_NAME);
			FileOutputStream out = new FileOutputStream(filepath);
			OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");
			writer.write(definestr, 0, definestr.length());
			writer.close();
			out.close();
			
			System.out.println(String.format("导出proto定义文件 %s\\%s.lua 成功!", EXPORT_LUA_PATH, EXPORT_LUA_NAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ProtoReader();
	}
}
