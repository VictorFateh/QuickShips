package dev_t.cs161.quickship;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class quickShipBluetoothPacketsToBeSent implements Parcelable {
    private int packetType;
    private String playerID;
    private String chatMessage;
    private int movesChosen;
    private boolean turnDone;
    private String emojiType;
    private boolean shipsPlaced;
    private String mBoard;
    private byte [] mBoardv2;
    static final int CHAT = 0;
    static final int SHIPS_PLACED = 1;
    static final int MOVES = 2;
    static final int TURN_DONE = 3;
    static final int GAME_WON = 4;
    static final int QUIT = 5;
    static final int NAME_CHANGE = 6;
    static final int DISCONNECTED = 7;

    public quickShipBluetoothPacketsToBeSent(int packetType, String stringType) {
        this.packetType = packetType;
        if (packetType == CHAT) {
            this.chatMessage = stringType;
        }
    }
    public quickShipBluetoothPacketsToBeSent(int packetType, byte [] byteArray) {
        this.packetType = packetType;
        if (packetType == SHIPS_PLACED) {
            this.mBoardv2 = byteArray;
        }
    }

    // Used for placing moves
    public quickShipBluetoothPacketsToBeSent(int packetType, int movesChosen, String emojiType) {
        this.packetType = packetType;
        this.movesChosen = movesChosen;
        this.emojiType = emojiType;
    }

    public quickShipBluetoothPacketsToBeSent(int packetType, boolean status) {
        this.packetType = packetType;
        if (packetType == TURN_DONE || packetType == DISCONNECTED) {
            this.turnDone = status;
        }
    }

    public String getBoard() {
        return mBoard;
    }

    public byte[] getBoardv2() {
        return mBoardv2;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(packetType);
        dest.writeString(mBoard);
        dest.writeString(playerID);
        dest.writeString(chatMessage);
        dest.writeInt(movesChosen);
        dest.writeByte((byte) (turnDone ? 1 : 0));
        dest.writeString(emojiType);
        dest.writeByte((byte) (shipsPlaced ? 1 : 0));
        dest.writeInt((mBoardv2 != null) ? mBoardv2.length : 0);
        dest.writeByteArray(mBoardv2);

    }

    protected quickShipBluetoothPacketsToBeSent(Parcel in) {
        packetType = in.readInt();
        mBoard = in.readString();
        playerID = in.readString();
        chatMessage = in.readString();
        movesChosen = in.readInt();
        turnDone = in.readByte() != 0;
        emojiType = in.readString();
        shipsPlaced = in.readByte() != 0;
        mBoardv2 = new byte[in.readInt()];
        if(mBoardv2.length > 0)
            in.readByteArray(mBoardv2);
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
