import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Vector;

class MultiChatServer {
    static List<Client> clientList = new Vector<>();

    static class Client {
        String name;
        Socket socket;
        DataInputStream dis;
        DataOutputStream dos;

        Client(String name, Socket socket) throws IOException {
            this.name = name;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        }
    }

    void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(4000)) {
            System.out.println("서버가 시작되었습니다. 총원" + (clientList.size()+1) + "명");
            new Thread(() -> speak()).start();

            while (true) {
                Socket socket = serverSocket.accept();
                String name = configName(socket);
                Client client = new Client(name, socket);
                clientList.add(client);
                System.out.println(name + "님이 접속하셨습니다. 총원" + (clientList.size()+1) +"명");
                new Thread(() -> listen(client)).start();
            }
        } catch (IOException e) {
        }
    }

    static String configName(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            return dis.readUTF();
        } catch (IOException e) {
        }
        return "Guest";
    }

    static void listen(Client client) {
        String msg;
        try {
            while (true) {
                msg = client.dis.readUTF();
                System.out.println(client.name + ">> " + msg);
                echo(client.name, msg);
            }
        } catch (IOException e) {
        } finally {
            try {
                client.dos.close();
                client.dis.close();
                client.socket.close();
            } catch (IOException e) {
            }
            clientList.remove(client);
        }
    }

    void speak() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while (true) {
                line = br.readLine();
                if(line.equals("Option")) {
                    System.out.println("1) list , 2) whisper, 3) drop ,4) quit");
                    String optionCommand = br.readLine();
                    if(optionCommand.equals("list")) {
                        for (Client client : clientList) {
                            System.out.println(client.name);
                        }
                    }else if(optionCommand.equals("whisper")) {
                        System.out.print("귀속말 할 유저: ");
                        String recipient = br.readLine();
                        System.out.println("귓속말 모드(" + recipient + ") 종료는 'exit' ㄱ");
                        while (true) {
                            line = br.readLine();
                            if (line.equalsIgnoreCase("exit")) {
                                break;
                            }
                            for (Client client : clientList) {
                                if (client.name.equals(recipient)) {
                                    try {
                                        client.dos.writeUTF("(귓속말) 관리자 >> " + line);
                                        client.dos.flush();
                                    } catch (IOException e) {
                                    }
                                    break;
                                }
                            }
                        }
                        System.out.println("귓속말 모드 종료");
                    }
                    else if(optionCommand.equals("drop")) {
                        System.out.print("강퇴할 유저 : ");
                        String dropUser = br.readLine();
                        drop(dropUser);
                    }else if(optionCommand.equals("quit")) {
                        System.out.println("Option 종료");
                    }else{
                        System.out.println("없는 옵션입니다.");
                    }
                } else {
                    echo("관리자", line);
                }
            }
        } catch (IOException e) {
        }
    }

    static void whisper(String sender, String recipient, String msg) {
        for (Client client : clientList) {
            if (client.name.equals(recipient)) {
                try {
                    client.dos.writeUTF("(귓속말) " + sender + ">> " + msg);
                    client.dos.flush();
                } catch (IOException e) {
                }
                return;
            }
        }
        System.out.println("사용자를 찾을 수 없습니다: " + recipient);
    }

    static void echo(String sender, String msg) {
        for (Client client : clientList) {
            try {
                client.dos.writeUTF(sender + ">> " + msg);
                client.dos.flush();
            } catch (IOException e) {
            }
        }
    }

    void drop(String name) {
        for (Client client : clientList) {
            if (client.name.equals(name)) {
                try {
                    client.dos.writeUTF("관리자에 의해 강퇴되었습니다.");
                    client.dos.flush();
                    client.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientList.remove(client);
                System.out.println(name + "님이 강퇴되었습니다.");
                echo("관리자", name + "님이 강퇴되었습니다. 총원" + (clientList.size()+1) +"명");
                return;
            }
        }
        System.out.println("사용자를 찾을 수 없습니다: " + name);
    }

    public static void main(String[] args) {
        MultiChatServer multiChatServer = new MultiChatServer();
        multiChatServer.startServer();
    }
}
