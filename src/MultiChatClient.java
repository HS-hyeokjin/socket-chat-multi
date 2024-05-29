import java.io.*;
import java.net.*;

class MultiChatClient {
    Socket s;
    String ipServer = "172.30.1.54"; //"127.0.0.1" == "localhost"
    int port = 4000;
    InputStream is;
    DataInputStream dis;
    OutputStream os;
    DataOutputStream dos;
    Thread listenThread = new Thread() {
        public void run() {
            listen();
        }
    };

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    MultiChatClient() {
        try {
            s = new Socket(ipServer, port);
            pln(ipServer + " 서버와 접속 성공");

            is = s.getInputStream();
            os = s.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            nameRequest();
            listenThread.start();
            speak();

        } catch (UnknownHostException ue) {
            pln("네트워상에 그런 ip와 port에서의 서버를 찾을 없음");
        } catch (IOException ie) {
        } finally {
            try {
                s.close();
                pln("서버와 접속 해제");
            } catch (IOException ie) {
            }
        }
    }

    void listen() {
        String msg;
        try {
            while (true) {
                msg = dis.readUTF();
                pln(msg);
            }
        } catch (IOException ie) {
        } finally {
            try {
                dis.close();
                is.close();
            } catch (IOException ie) {
            }
        }
    }

    void speak() {
        String line = null;
        try {
            while (true) {
                line = br.readLine();
                dos.writeUTF(line);
                dos.flush();
            }
        } catch (IOException ie) {
        } finally {
            try {
                dos.close();
                os.close();
                br.close();
            } catch (IOException ie) {
            }
        }
    }

    void nameRequest() {
        String line;
        try {
            System.out.print("이름을 설정해 주세요 : ");
            line = br.readLine();
            if(line.isEmpty()){
                line = "Guest";
            }
            dos.writeUTF(line);
            dos.flush();
        } catch (IOException ie) {
        }
    }

        void pln (String str){
            System.out.println(str);
        }

        public static void main (String[]args){
            new MultiChatClient();
        }
    }