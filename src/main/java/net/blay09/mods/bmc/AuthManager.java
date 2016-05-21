package net.blay09.mods.bmc;

import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;

public class AuthManager {

	public static class TokenPair {
		private final String username;
		private final String token;

		public TokenPair(String username, String token) {
			this.username = username;
			this.token = token;
		}

		public String getUsername() {
			return username;
		}

		public String getToken() {
			return token;
		}
	}

	private static Map<String, TokenPair> tokenMap = Maps.newHashMap();

	public static TokenPair getToken(String id) {
		return tokenMap.get(id);
	}

	public static void storeToken(String id, String username, String token) {
		tokenMap.put(id, new TokenPair(username, token));
		save();
	}

	public static void load() {
		File userHome = new File(System.getProperty("user.home"));
		try(DataInputStream in = new DataInputStream(new FileInputStream(new File(userHome, ".bmc-auth.dat")))) {
			int count = in.readByte();
			for(int i = 0; i < count; i++) {
				storeToken(in.readUTF(), in.readUTF(), in.readUTF());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void save() {
		File userHome = new File(System.getProperty("user.home"));
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(userHome, ".bmc-auth.dat")))) {
			out.writeByte(tokenMap.size());
			for(Map.Entry<String, TokenPair> entry : tokenMap.entrySet()) {
				out.writeUTF(entry.getKey());
				out.writeUTF(entry.getValue().username);
				out.writeUTF(entry.getValue().token);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
