package dev_t.cs161.quickship;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by trinhnguyen on 3/30/17.
 */

public class quickShipBluetoothPacketsToBeSent implements Parcelable {
    private PacketType packetType;
    private String playerID;
    private String chatMessage;
    private int movesChosen;
    private boolean turnDone;
    private String emojiType;
    private boolean shipsPlaced;

    public quickShipBluetoothPacketsToBeSent(PacketType packetType, String chatMessage) {
        this.packetType = packetType;
        this.chatMessage = chatMessage;
    }

    public quickShipBluetoothPacketsToBeSent(PacketType packetType, String playerID, int movesChosen, String emojiType) {
        this.packetType = packetType;
        this.playerID = playerID;
        this.movesChosen = movesChosen;
        this.emojiType = emojiType;
    }

    public quickShipBluetoothPacketsToBeSent(PacketType packetType, boolean status) {
        this.packetType = packetType;
        if (packetType.equals(PacketType.TURN_DONE)) {
            this.turnDone = status;
        } else if (packetType.equals(PacketType.SHIPS_PLACED)) {
            this.shipsPlaced = status;
        }
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public int getMovesChosen() {
        return movesChosen;
    }

    public void setMovesChosen(int movesChosen) {
        this.movesChosen = movesChosen;
    }

    public boolean isTurnDone() {
        return turnDone;
    }

    public void setTurnDone(boolean turnDone) {
        this.turnDone = turnDone;
    }

    public String getEmojiType() {
        return emojiType;
    }

    public void setEmojiType(String emojiType) {
        this.emojiType = emojiType;
    }

    public boolean isShipsPlaced() {
        return shipsPlaced;
    }

    public void setShipsPlaced(boolean shipsPlaced) {
        this.shipsPlaced = shipsPlaced;
    }

    protected quickShipBluetoothPacketsToBeSent(Parcel in) {
        playerID = in.readString();
        chatMessage = in.readString();
        movesChosen = in.readInt();
        turnDone = in.readByte() != 0;
        emojiType = in.readString();
        shipsPlaced = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerID);
        dest.writeString(chatMessage);
        dest.writeInt(movesChosen);
        dest.writeByte((byte) (turnDone ? 1 : 0));
        dest.writeString(emojiType);
        dest.writeByte((byte) (shipsPlaced ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<quickShipBluetoothPacketsToBeSent> CREATOR = new Creator<quickShipBluetoothPacketsToBeSent>() {
        @Override
        public quickShipBluetoothPacketsToBeSent createFromParcel(Parcel in) {
            return new quickShipBluetoothPacketsToBeSent(in);
        }

        @Override
        public quickShipBluetoothPacketsToBeSent[] newArray(int size) {
            return new quickShipBluetoothPacketsToBeSent[size];
        }
    };
}
