package net.blay09.mods.bmc.api;

public interface IChatChannel {

	String getName();

	void clearChat();

	boolean hasUnreadMessages();

	void setOutgoingPrefix(String outgoingPrefix);

	String getOutgoingPrefix();

	void setMuted(boolean muted);

	boolean isMuted();

	boolean isHidden();

	boolean isManaged();

	IChatMessage getChatLine(int id);

	void setFilterPattern(String pattern);

	String getFilterPattern();

	boolean isExclusive();

	void setExclusive(boolean exclusive);

	MessageStyle getMessageStyle();

	void setMessageStyle(MessageStyle messageStyle);

	void setFormat(String format);

	void disableDefaultNameTransformer();
}
