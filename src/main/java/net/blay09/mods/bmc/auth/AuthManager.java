package net.blay09.mods.bmc.auth;

import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;

public class AuthManager {

	private final Map<String, TokenPair> tokenMap = Maps.newHashMap();

	public TokenPair getToken(String id) {
		return tokenMap.get(id);
	}

	public void storeToken(String id, String username, String token) {
		tokenMap.put(id, new TokenPair(username, token));
		save();
	}

	public void load() {
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

	private void save() {
		File userHome = new File(System.getProperty("user.home"));
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(userHome, ".bmc-auth.dat")))) {
			out.writeByte(tokenMap.size());
			for(Map.Entry<String, TokenPair> entry : tokenMap.entrySet()) {
				out.writeUTF(entry.getKey());
				out.writeUTF(entry.getValue().getUsername());
				out.writeUTF(entry.getValue().getToken());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
