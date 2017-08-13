package net.blay09.mods.chattweaks.auth;

import com.google.common.collect.Maps;
import net.blay09.mods.chattweaks.ChatTweaks;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthManager {

	private final Map<String, TokenPair> tokenMap = Maps.newHashMap();

	public TokenPair getToken(String id) {
		return tokenMap.get(id);
	}

	public void storeToken(String id, String username, String token, boolean doNotSave) {
		tokenMap.put(id, new TokenPair(username, token, doNotSave));
		save();
	}

	public void load() {
		File userHome = new File(System.getProperty("user.home"));
		try(DataInputStream in = new DataInputStream(new FileInputStream(new File(userHome, ".chattweaks-auth.dat")))) {
			int count = in.readByte();
			for(int i = 0; i < count; i++) {
				storeToken(in.readUTF(), in.readUTF(), in.readUTF(), false);
			}
		} catch(FileNotFoundException ignored) {
		} catch (IOException e) {
			ChatTweaks.logger.error("An error occurred when trying to load authentication data: ", e);
		}
	}

	private void save() {
		File userHome = new File(System.getProperty("user.home"));
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(userHome, ".chattweaks-auth.dat")))) {
			List<Map.Entry<String, TokenPair>> list = tokenMap.entrySet().stream().filter(p -> !p.getValue().isDoNotStore()).collect(Collectors.toList());
			out.writeByte(list.size());
			for(Map.Entry<String, TokenPair> entry : list) {
				out.writeUTF(entry.getKey());
				out.writeUTF(entry.getValue().getUsername());
				out.writeUTF(entry.getValue().getToken());
			}
		} catch (IOException e) {
			ChatTweaks.logger.error("An error occurred when trying to save authentication data: ", e);
		}
	}

}
