package org.kia.kalah.dto;

import lombok.Data;

/**
 * this class is created as a pit to keep the stones
 */
/**
 * @Data is lambok annotation for generating getters and setters at compile time
 */

@Data
public class Pit {
    private Integer stones = 0;

    public void addStones(int stones)
    {
        this.stones += stones;
    }
    public int removeStones() {
        int stones = this.stones;
        this.stones = 0;
        return stones;
    }

    @Override
    public String toString() {
        return "Pit{" +
                "stones=" + stones +
                '}';
    }
}
