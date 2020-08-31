package application;

import com.google.common.eventbus.EventBus;

import java.util.ArrayList;

public class Channel {
    private EventBus channel;
    private ArrayList<Participant> participants;

    public Channel(String name, Participant p1, Participant p2) {
        participants = new ArrayList<>();
        channel = new EventBus(name);
        channel.register(p1);
        participants.add(p1);
        channel.register(p2);
        participants.add(p2);
    }

    public Channel(String name) {
        channel = new EventBus(name);
    }

    public String GetName() {
        return channel.identifier();
    }

    public Participant GetParticipant(int index) {
        return participants.get(index);
    }

    public void RegisterParticipant(Participant participant) {
        participants.add(participant);
        channel.register(participant);
    }

    public void SendMessage(Message message) {
        channel.post(message);
    }

}
