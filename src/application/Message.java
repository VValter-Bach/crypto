package application;

public class Message {
    int id;
    String content;
    String algorithm;
    String keyFileName;
    String sender;
    String receiver;

    public Message(String content, String algorithm, String keyFileName, String sender, String receiver) {
        id = Company.messageIDs++;
        this.content = content;
        this.algorithm = algorithm;
        this.keyFileName = keyFileName;
        this.sender = sender;
        this.receiver = receiver;
    }
}
