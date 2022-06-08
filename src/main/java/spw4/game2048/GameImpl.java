package spw4.game2048;

import java.util.Random;
import java.util.function.Function;

public class GameImpl implements Game {
    public static Random random;

    private final int size = 4;

    private final int[][] board;
    private int score;
    private int moves;

    public GameImpl() {
        board = new int[size][size];
    }

    public int getMoves() {
        return moves;
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
                if (board[i][o] == 2048) return true;

                if ((o < size - 1 && board[i][o + 1] == board[i][o])
                        || (o > 0 && board[i][o - 1] == board[i][o])
                        || (i < size - 1 && board[i + 1][o] == board[i][o])
                        || (i > 0 && board[i - 1][o] == board[i][o])) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWon() {
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                if (board[i][k] == 2048)
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
        StringBuilder returnVal = new StringBuilder(String.format("Moves: %s\t\tScore: %d\n", moves, score));

        for (int i = 0; i < size; i++) {
            StringBuilder toAdd = new StringBuilder();
            for (int k = 0; k < size; k++) {
                toAdd.append(String.format("%-5s", board[i][k] == 0 ? "." : board[i][k]));
            }
            returnVal.append(toAdd.toString().trim());
            returnVal.append('\n');
        }

        return returnVal.toString().trim();
    }

    public void initialize() {
        placeRandomTile();
        placeRandomTile();
    }

    public void move(Direction direction) {
        boolean hasMovedAnything = false;

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
                    if (yPosToShiftFrom == yPosToShiftTo && xPosToShiftFrom == xPosToShiftTo)
                        continue;
                    if (board[yPosToShiftFrom][xPosToShiftFrom] != 0) {
                        if (board[yPosToShiftTo][xPosToShiftTo] == board[yPosToShiftFrom][xPosToShiftFrom] && !summedUpFields[yPosToShiftTo][xPosToShiftTo]) {
                            board[yPosToShiftTo][xPosToShiftTo] *= 2;
                            board[yPosToShiftFrom][xPosToShiftFrom] = 0;
                            score += board[yPosToShiftTo][xPosToShiftTo];
                            summedUpFields[yPosToShiftTo][xPosToShiftTo] = true;
                            hasMovedAnything = true;
                        } else if (board[yPosToShiftTo][xPosToShiftTo] == 0) {
                            board[yPosToShiftTo][xPosToShiftTo] = board[yPosToShiftFrom][xPosToShiftFrom];
                            board[yPosToShiftFrom][xPosToShiftFrom] = 0;
                            hasMovedAnything = true;
                        }
                    }
                }
            }
        }

        if (hasMovedAnything) {
            moves++;
            placeRandomTile();
        }
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
