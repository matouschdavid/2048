package spw4.game2048;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

public class GameImpl implements Game {
    public static Random random;
    private final int size = 4;
    private final int[][] board;
    private int score;

    public GameImpl() {
        board = new int[size][size];
    }

    public int getMoves() {
        // to do ...
        return 0;
    }

    public int getScore() {
        return score;
    }

    public int getValueAt(int x, int y) {
        return board[y][x];
    }


    public boolean isOver() {
        for (int i = 0; i < size; i++) {
            for (int o = 0; o < size; o++) {
                if (board[i][o] == 0) return false;

                if ((o < size - 1 && board[i][o+1] == board[i][o])
                        || (o > 0 && board[i][o-1] == board[i][o])
                        || (i < size -1 && board[i+1][o] == board[i][o])
                        || (i > 0 && board[i-1][o] == board[i][o])) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWon() {
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                if(board[i][k] == 2048)
                    return true;
            }
        }
        return false;
    }

    @Override
    public int getBoardSize() {
        return size;
    }

    @Override
    public String toString() {
        // to do ...
        return "";
    }

    public void initialize() {
        placeRandomTile();
        placeRandomTile();
    }

    public void move(Direction direction) {
        boolean isVertical = direction == Direction.up || direction == Direction.down;

        int startValue = 0;
        int increment = 1;
        Function<Integer, Boolean> condition = (Integer i) -> i < size;

        switch (direction) {
            case right, down -> {
                startValue = size - 1;
                increment = -1;
                condition = (Integer i) -> i >= 0;
            }
        }

        var summedUpFields = new boolean[size][size];
        for (int yPosToShiftTo = startValue; condition.apply(yPosToShiftTo); yPosToShiftTo += increment) {
            for (int xPosToShiftTo = startValue; condition.apply(xPosToShiftTo); xPosToShiftTo += increment) {
                for (int cursorPos = isVertical ? yPosToShiftTo : xPosToShiftTo; condition.apply(cursorPos); cursorPos += increment) {
                    int yPosToShiftFrom = yPosToShiftTo;
                    int xPosToShiftFrom = cursorPos;


                    if (isVertical) {
                        xPosToShiftFrom = xPosToShiftTo;
                        yPosToShiftFrom = cursorPos;
                    }
                    if(yPosToShiftFrom == yPosToShiftTo && xPosToShiftFrom == xPosToShiftTo)
                        continue;
                    if (board[yPosToShiftFrom][xPosToShiftFrom] != 0) {
                        if (board[yPosToShiftTo][xPosToShiftTo] == board[yPosToShiftFrom][xPosToShiftFrom] && !summedUpFields[yPosToShiftTo][xPosToShiftTo]) {
                            board[yPosToShiftTo][xPosToShiftTo] *= 2;
                            board[yPosToShiftFrom][xPosToShiftFrom] = 0;
                            score += board[yPosToShiftTo][xPosToShiftTo];
                            summedUpFields[yPosToShiftTo][xPosToShiftTo] = true;
                        } else if(board[yPosToShiftTo][xPosToShiftTo] == 0) {
                            board[yPosToShiftTo][xPosToShiftTo] = board[yPosToShiftFrom][xPosToShiftFrom];
                            board[yPosToShiftFrom][xPosToShiftFrom] = 0;
                        }
                    }
                }
            }
        }

        placeRandomTile();
    }

    public void placeRandomTile() {
        int nextX;
        int nextY;
        do {
            nextY = random.nextInt(size);
            nextX = random.nextInt(size);
        } while (board[nextY][nextX] != 0);

        board[nextY][nextX] = random.nextInt(10) == 0 ? 4 : 2;
    }

    public void placeTile(int x, int y, int value) {

        board[y][x] = value;
    }
}
