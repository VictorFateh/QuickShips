package devt.cs161.quickbattleship;

/**
 * Created by trinhnguyen on 3/12/17.
 */

public class quickbattleship_board_slot {
    private boolean isHit;
    private boolean isOccupied;

    public quickbattleship_board_slot() {
        setHit(false);
        setOccupied(false);
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}