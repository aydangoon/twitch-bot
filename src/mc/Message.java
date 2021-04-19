package mc;

// A twitch.tv message. 
public class Message {

	private String username;
	private boolean mod;
	private boolean sub;
	private boolean bot;
	private int bits;
	private String text;
	
	public Message(String username, boolean mod, boolean sub, boolean bot, int bits, String text) {
		this.username = username;
		this.mod = mod;
		this.sub = sub;
		this.bot = bot;
		this.bits = bits;
		this.text = text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isMod() {
		return mod;
	}

	public void setMod(boolean mod) {
		this.mod = mod;
	}

	public boolean isSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}

	public boolean isBot() {
		return bot;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	public int getBits() {
		return bits;
	}

	public void setBits(int bits) {
		this.bits = bits;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		return "Message [username=" + username + ", mod=" + mod + ", sub=" + sub + ", bot=" + bot + ", bits=" + bits
				+ ", text=" + text + "]";
	}
	
}
