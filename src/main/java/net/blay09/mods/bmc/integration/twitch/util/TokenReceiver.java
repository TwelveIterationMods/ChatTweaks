/*
Code adapted from ChattyWebserver, though partly rewritten to be less awful.

Original License:

Copyright (c) 2014 tduva

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package net.blay09.mods.bmc.integration.twitch.util;

import com.google.common.collect.Lists;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public abstract class TokenReceiver implements Runnable {

	private static final int SO_TIMEOUT = 10 * 1000;
	public static final int PORT = 61324;

	private final Thread thread;
	private volatile boolean running;
	private ServerSocket serverSocket;

	private final List<Connection> connections = Lists.newArrayList();

	public TokenReceiver() {
		thread = new Thread(this);
	}

	public void start() {
		running = true;
		thread.start();
	}

	public abstract void onTokenReceived(String token);

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress());
		} catch (IOException e) {
			e.printStackTrace();
			stop();
			return;
		}

		while (running) {
			try {
				connections.add(new Connection(serverSocket.accept()));
			} catch (SocketException e) {
				break;
			} catch (IOException e) {
				break;
			}
		}

		closeSockets();
	}

	public void closeSockets() {
		for(Connection connection : connections) {
			connection.abort();
		}
		connections.clear();
		if(serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ignored) {}
		}
	}

	public void stop() {
		running = false;
		closeSockets();
	}

	private static String parseToken(String request) {
		int start = request.indexOf("/token/");
		if (start == -1) {
			return "";
		}
		start += "/token/".length();
		int end = request.indexOf(" ", start);
		int end2 = request.indexOf("/", start);
		if (end2 != -1 && end2 < end) {
			end = end2;
		}
		if (end == -1) {
			return "";
		}
		return request.substring(start, end).trim();
	}

	private static String sendFile(String fileName) {
		if (fileName == null) {
			return makeHeader(false) + "Nothing here...";
		}
		String content;
		try {
			InputStream input = TokenReceiver.class.getResourceAsStream(fileName);
			if(input != null) {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
				StringBuilder buffer = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					buffer.append(line);
					buffer.append("\n");
				}
				content = buffer.toString();
			} else {
				content = "An error occurred (couldn't read file)";
			}
		} catch (IOException e) {
			e.printStackTrace();
			content = "An error occurred (couldn't read file)";
		}
		return makeHeader(true) + content;
	}

	private static String makeHeader(boolean ok) {
		String header = "";
		if (ok) {
			header += "HTTP/1.0 200 OK\n";
		} else {
			header += "HTTP/1.0 403 Forbidden\n";
		}
		header += "Server: TwitchTokenReceiver\n";
		header += "Content-Type: text/html; charset=UTF-8\n\n";
		return header;
	}

	private class Connection implements Runnable {
		private final Thread thread;
		private final Socket socket;

		public Connection(Socket socket) {
			this.socket = socket;
			thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {
			try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				socket.setSoTimeout(SO_TIMEOUT);
				String request = input.readLine();
				if (request != null) {
					try (OutputStream output = socket.getOutputStream()) {
						String response;
						if (request.toLowerCase().startsWith("get /token/")) {
							String token = parseToken(request);
							if (token.isEmpty()) {
								response = sendFile("/token_redirect.html");
							} else {
								response = sendFile("/token_received.html");
								onTokenReceived(token);
							}
						} else if (request.toLowerCase().startsWith("get /tokenreceived/")) {
							response = sendFile("/token_received_no_redirect.html");
						} else {
							response = sendFile(null);
						}
						output.write(response.getBytes("UTF-8"));
					} catch (IOException ex) {
						System.err.println("Error responding: " + ex.getLocalizedMessage());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void abort() {
			try {
				socket.close();
			} catch (IOException ignored) {}
		}
	}

}
