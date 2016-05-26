package net.blay09.mods.bmc;

import com.google.common.collect.Maps;
import net.blay09.mods.bmc.api.IAuthManager;
import net.blay09.mods.bmc.api.TokenPair;

import java.io.*;
import java.util.Map;

public class AuthManager implements IAuthManager {

	private Map<String, TokenPair> tokenMap = Maps.newHashMap();

	@Override
	public TokenPair getToken(String id) {
		return tokenMap.get(id);
	}

	@Override
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
