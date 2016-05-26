package net.blay09.mods.bmc.api;

public interface IAuthManager {
	void storeToken(String id, String username, String token);
	TokenPair getToken(String id);
}
