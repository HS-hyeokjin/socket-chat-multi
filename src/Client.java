import java.net.Socket;

class Client {
    String name;
    Socket socket;
    boolean isWhisper;

    Client(String name, Socket socket, boolean isWhisper) {
        this.name = name;
        this.socket = socket;
        this.isWhisper = isWhisper;
    }

}
