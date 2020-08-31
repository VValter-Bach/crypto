package application;

import configuration.Configuration;
import persistence.HSQLDB;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Company {
    public static int messageIDs = 0;
    private javafx.scene.control.TextArea outputArea;
    private boolean isDebug;
    private ArrayList<Participant> participants;
    private ArrayList<Channel> channels;

    public Company(javafx.scene.control.TextArea outputArea) {
        this.outputArea = outputArea;
        isDebug = false;
        participants = new ArrayList<>();
        channels = new ArrayList<>();
    }

    public static String getUnixTime() {
        Date now = new Date();
        Long longTime = now.getTime() / 1000;
        return longTime.toString();
    }

    public void ToggleDebug() {
        isDebug = !isDebug;
    }

    public void Parser(String lines) {
        String[] inputs = lines.split("\n");
        for (String input : inputs) {
            outputArea.appendText(input + ":\n");
            try {
                if (input.contains("show algorithm")) {
                    ShowAlgorithms();
                } else if (input.contains("encrypt message")) {
                    input = input.replace("encrypt message \"", "");
                    input = input.replace("\" using ", ";");
                    input = input.replace(" and keyfile ", ";");
                    String[] vars = input.split(";");
                    EncryptMessage(vars[0], vars[1], vars[2]);
                } else if (input.contains("decrypt message")) {
                    input = input.replace("decrypt message \"", "");
                    input = input.replace("\" using ", ";");
                    input = input.replace(" and keyfile ", ";");
                    String[] vars = input.split(";");
                    DecryptMessage(vars[0], vars[1], vars[2]);
                } else if (input.contains("crack encrypted message")) {
                    input = input.replace("crack encrypted message \"", "");
                    input = input.replace("\" using ", ";");
                    String[] vars = input.split(";");
                    Crack(vars[0], vars[1]);
                } else if (input.contains("register participant")) {
                    input = input.replace("register participant ", "");
                    input = input.replace(" with type ", ";");
                    String[] vars = input.split(";");
                    AddParticipant(vars[0], vars[1]);
                } else if (input.contains("create channel")) {
                    input = input.replace("create channel ", "");
                    input = input.replace(" from ", ";");
                    input = input.replace(" to ", ";");
                    String[] vars = input.split(";");
                    AddChannel(vars[0], vars[1], vars[2]);
                } else if (input.contains("show channel")) {
                    ShowChannels();
                } else if (input.contains("drop channel")) {
                    input = input.replace("drop channel ", "");
                    DropChannel(input);
                } else if (input.contains("intrude channel")) {
                    input = input.replace("intrude channel ", "");
                    input = input.replace(" by ", ";");
                    String[] vars = input.split(";");
                    IntrudeChannel(vars[0], vars[1]);
                } else if (input.contains("send message")) {
                    input = input.replace("send message \"", "");
                    input = input.replace("\" from ", ";");
                    input = input.replace(" to ", ";");
                    input = input.replace(" using ", ";");
                    input = input.replace(" and keyfile ", ";");
                    String[] vars = input.split(";");
                    SendMessage(vars[0], vars[1], vars[2], vars[3], vars[4]);
                } else {
                    outputArea.appendText("INVALID INPUT!!!!!\n");
                }

            } catch (Exception e) {
                outputArea.appendText("INVALID INPUT!!!!! lead to error:\n");
                outputArea.appendText(e.getMessage());
            }
            outputArea.appendText("-------------------------\n");
        }
    }

    public void PrintLatestLog() {
        outputArea.appendText("Reading latest Log-File:\n");
        try (Stream<Path> walk = Files.walk(Paths.get(Configuration.instance.logDirectory))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            ArrayList<Long> times = new ArrayList<>();

            long high = 0L;
            int index = 0;
            for (int i = 0; i < result.size(); i++) {
                long l = Long.parseLong(result.get(i).substring(result.get(i).lastIndexOf("_") + 1, result.get(i).lastIndexOf(".")));
                if (l > high) {
                    high = l;
                    index = i;
                }
            }
            outputArea.appendText(Files.readString(Path.of(result.get(index))));

        } catch (Exception e) {
            outputArea.appendText("Cant Read Log Files\n");

        }
        outputArea.appendText("-------------------------\n");
    }

    public ArrayList<Participant> GetParticipants() {
        return participants;
    }

    private Participant AddParticipant(String name, String type) {
        int id = HSQLDB.instance.getNextID("participants") + 1;
        for (Participant value : participants) {
            if (value.name.equals(name)) {
                outputArea.appendText("participant " + name + " already exists, using existing postbox_" + name + "\n");
                return null;
            }
        }
        Participant participant = null;
        if (type.equals("normal")) participant = new Participant(name, Type.NORMAL, id, outputArea);
        else if (type.equals("intruder")) participant = new Participant(name, Type.INTRUDER, id, outputArea);
        else {
            outputArea.appendText("unknown type\n");
            return null;
        }
        participants.add(participant);
        // adding to list of participants
        HSQLDB.instance.insertDataTableParticipants(name, type);
        // creating Postbox
        HSQLDB.instance.createTablePostBox(name);
        outputArea.appendText("participant " + name + " with type " + type + " registered and postbox_ + " + name + " created\n");
        return participant;

    }

    private Channel AddChannel(String name, String participant1, String participant2) {
        Participant p1 = GetParticipant(participant1);
        Participant p2 = GetParticipant(participant2);
        if (p1 == null || p2 == null) {
            outputArea.appendText("Participant " + participant1 + " and/or " + participant2 + " does not exist\n");
            return null;
        }
        if (p1.equals(p2)) {
            outputArea.appendText(participant1 + " and " + p2.name + " are identical, cannot create channel on itself\n");
            return null;
        }
        for (Channel channel : channels) {
            if (channel.GetName().equals(name)) {
                outputArea.appendText("channel " + name + " already exists\n");
                return null;
            }
            if ((channel.GetParticipant(0).equals(p1) && channel.GetParticipant(1).equals(p2)) ||
                    (channel.GetParticipant(1).equals(p1) && channel.GetParticipant(0).equals(p2))) {
                outputArea.appendText("communication channel between " + p1.name + " and " + p2.name + " already exists\n");
                return null;
            }
        }

        Channel channel = new Channel(name, p1, p2);
        HSQLDB.instance.insertDataTableChannels(name, participant1, participant2);
        channels.add(channel);
        outputArea.appendText("channel " + name + " from " + participant1 + " to " + participant2 + " successfully created\n");
        return channel;
    }

    private void ShowChannels() {
        for (Channel channel : channels) {
            outputArea.appendText(channel.GetName() + " | " + channel.GetParticipant(0).name + " and " + channel.GetParticipant(1).name + "\n");
        }
    }

    private void DropChannel(String name) {
        for (Channel channel : channels) {
            if (!channel.GetName().equals(name)) continue;
            channels.remove(channel);
            HSQLDB.instance.dropDataTableChannels(name);
            outputArea.appendText("channel " + name + " deleted\n");
            return;
        }
        outputArea.appendText("unknown channel " + name + "\n");
    }

    private void SendMessage(String message, String participant1, String participant2, String algorithm, String keyfile) {
        Participant p1 = GetParticipant(participant1);
        Participant p2 = GetParticipant(participant2);
        if (p1 == null || p2 == null) {
            outputArea.appendText("Participant " + participant1 + " and/or " + participant2 + " does not exist\n");
            return;
        }
        for (Channel channel : channels) {
            if ((channel.GetParticipant(0).equals(p1) && channel.GetParticipant(1).equals(p2)) ||
                    (channel.GetParticipant(1).equals(p1) && channel.GetParticipant(0).equals(p2))) {
                String emessage = Encrypt(message, algorithm, keyfile);
                Message msg = new Message(emessage, algorithm, keyfile, participant1, participant2);
                HSQLDB.instance.insertDataTableMessages(msg.sender, msg.receiver, message, msg.algorithm, emessage, msg.keyFileName, Long.parseLong(getUnixTime()));
                channel.SendMessage(msg);
            }
        }
    }

    private void ShowAlgorithms() {
        File folder = new File(Configuration.instance.componentDirectory);
        for (final File entry : Objects.requireNonNull(folder.listFiles())) {
            if (entry.getName().endsWith(".jar")) outputArea.appendText(entry.getName().replace(".jar", "\n"));
        }
    }

    private void IntrudeChannel(String cname, String pname) {
        Channel channel = GetChannel(cname);
        Participant participant = GetParticipant(pname);
        if (channel == null) {
            outputArea.appendText("unknown channel " + cname + "\n");
        } else if (participant == null) {
            outputArea.appendText("unknown participant " + pname + "\n");
        }
        outputArea.appendText("channel " + cname + " got successfully intruded by " + pname + "\n");
        channel.RegisterParticipant(participant);
    }

    private void EncryptMessage(String message, String algorithm, String fileName) {
        FileWriter fw = null;
        if (isDebug) {
            File file = new File(Configuration.instance.logDirectory + "encrypt_" + algorithm + "_" + getUnixTime() + ".txt");
            try {
                file.createNewFile();
                fw = new FileWriter(file);
                fw.append("encrypting Message " + message + " using " + algorithm + " and key file with name " + fileName + "\n");
            } catch (Exception e) {
                System.out.println("Cant write logfile! :(");
            }
        }
        String msg = Encrypt(message, algorithm, fileName);
        outputArea.appendText(msg + "\n");
        try {
            if (fw == null) return;
            fw.append("encrypted Message to " + msg + "\n");
            fw.close();
        } catch (Exception e) {
            return;
        }
    }

    private void DecryptMessage(String message, String algorithm, String fileName) {
        FileWriter fw = null;
        if (isDebug) {
            File file = new File(Configuration.instance.logDirectory + "decrypt_" + algorithm + "_" + getUnixTime() + ".txt");
            try {
                file.createNewFile();
                fw = new FileWriter(file);
                fw.append("decrypting Message " + message + " using " + algorithm + " and key file with name " + fileName + "\n");
            } catch (Exception e) {
                System.out.println("Cant write logfile! :(");
            }
        }
        String msg = Decrypt(message, algorithm, fileName);
        outputArea.appendText(msg + "\n");
        try {
            if (fw == null) return;
            fw.append("decrypted Message to " + msg + "\n");
            fw.close();
        } catch (Exception e) {
            return;
        }

    }

    private void Crack(String message, String algorithm) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> task = new Callable<Object>() {
            public Object call() {
                Cryptography c = new Cryptography(algorithm + "_cracker");
                return c.Execute(message);
            }
        };
        Future<Object> future = executor.submit(task);
        try {
            String msg = (String) future.get(30, TimeUnit.SECONDS);
            outputArea.appendText(msg + "\n");
        } catch (TimeoutException ex) {
            outputArea.appendText("cracking encrypted message " + message + "failed\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String Encrypt(String message, String algorithm, String fileName) {
        Cryptography c = new Cryptography(algorithm);
        return c.Execute("encrypt", message, fileName);
    }

    private String Decrypt(String message, String algorithm, String fileName) {
        Cryptography c = new Cryptography(algorithm);
        return c.Execute("decrypt", message, fileName);
    }

    private Participant GetParticipant(String name) {
        for (Participant participant : participants) {
            if (participant.name.equals(name)) return participant;
        }
        return null;
    }

    private Channel GetChannel(String name) {
        for (Channel c : channels) {
            if (c.GetName().equals(name)) return c;
        }
        return null;
    }
}
