package net.blay09.mods.bmc.chat;

import java.io.*;
import java.util.Properties;

public class ChatMacros {

	private static final String[] chatMacro = new String[4];
	private static final Properties prop = new Properties();
	private static File macrosFile;

	public static void load(File file) {
		ChatMacros.macrosFile = file;
		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();
		try(FileReader reader = new FileReader(file)) {
			prop.load(reader);
		} catch (IOException ignored) {}
		chatMacro[0] = prop.getProperty("f5");
		chatMacro[1] = prop.getProperty("f6");
		chatMacro[2] = prop.getProperty("f7");
		chatMacro[3] = prop.getProperty("f8");
	}

	public static void save(File file) {
		try(FileWriter writer = new FileWriter(file)) {
			prop.store(writer, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getChatMacro(int index) {
		return chatMacro[index];
	}

	public static void setChatMacro(int index, String text) {
		chatMacro[index] = text;
		prop.setProperty("f" + (index + 5), text);
		save(macrosFile);
	}
}
