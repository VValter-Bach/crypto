package application;

import com.google.common.eventbus.Subscribe;
import configuration.Configuration;
import persistence.HSQLDB;

import java.util.concurrent.*;

import static application.Company.getUnixTime;

public class Participant {
    javafx.scene.control.TextArea outputArea;
    String name;
    Type type;
    int id;

    public Participant(String name, Type type, int id, javafx.scene.control.TextArea outputArea) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.outputArea = outputArea;
    }

    @Subscribe
    public void handleMessage(Message message) {
        //Intruder
        if (this.type == Type.INTRUDER) {
            String msg = null;
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Object> task = new Callable<Object>() {
                public Object call() {
                    Cryptography c = new Cryptography(message.algorithm + "_cracker");
                    return c.Execute(message.content);
                }
            };
            Future<Object> future = executor.submit(task);
            try {
                msg = (String) future.get(30, TimeUnit.SECONDS);
                if (msg.equals("unknown")) throw new TimeoutException();
                outputArea.appendText("intruder " + this.name + " cracked message from participant " + message.sender + " | " + msg + "\n");
            } catch (TimeoutException ex) {
                msg = "unknown";
                outputArea.appendText("intruder " + this.name + " | crack message from participant " + message.sender + " failed \n");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else if (!message.sender.equals(this.name)) {
            String msg;
            Cryptography c = new Cryptography(message.algorithm);
            if (message.algorithm.equals("rsa"))
                msg = c.Execute("decrypt", message.content, Configuration.instance.rsaDecryptFile);
            else msg = c.Execute("decrypt", message.content, message.keyFileName);

            outputArea.appendText(this.name + " received a new message\n");

            HSQLDB.instance.insertDataTablePostBox(this.name, message.algorithm, msg, Integer.parseInt(getUnixTime()));

        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
