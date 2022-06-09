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
        if (isWon())
            return true;

        for (int i = 0; i < size; i++) {
            for (int o = 0; o < size; o++) {
                if (board[i][o] == 0) return false;

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
        if (random == null)
            random = new Random();
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
        for (int yPosToShiftFrom = startValue; condition.apply(yPosToShiftFrom); yPosToShiftFrom += increment) {
            for (int xPosToShiftFrom = startValue; condition.apply(xPosToShiftFrom); xPosToShiftFrom += increment) {
                if (board[yPosToShiftFrom][xPosToShiftFrom] != 0) {
                    boolean movePossible = true;
                    int cursorPos = isVertical ? yPosToShiftFrom : xPosToShiftFrom;
                    while (movePossible && cursorPos >= 0 && cursorPos < size) {
                        int yPosToShiftTo = yPosToShiftFrom;
                        int xPosToShiftTo = cursorPos;
                        if (isVertical) {
                            yPosToShiftTo = cursorPos;
                            xPosToShiftTo = xPosToShiftFrom;
                        }

                        cursorPos -= increment;

                        if (xPosToShiftFrom == xPosToShiftTo && yPosToShiftFrom == yPosToShiftTo) continue;

                        int fromValue = board[yPosToShiftFrom][xPosToShiftFrom];
                        int toValue = board[yPosToShiftTo][xPosToShiftTo];

                        if (fromValue == toValue && !summedUpFields[yPosToShiftTo][xPosToShiftTo]) {
                            handleMerge(xPosToShiftFrom, xPosToShiftTo, yPosToShiftFrom, yPosToShiftTo);
                            summedUpFields[yPosToShiftTo][xPosToShiftTo] = true;
                            hasMovedAnything = true;
                        } else if (toValue == 0) {
                            handleNormalMove(xPosToShiftFrom, xPosToShiftTo, yPosToShiftFrom, yPosToShiftTo);
                            hasMovedAnything = true;
                            if (isVertical) {
                                yPosToShiftFrom = cursorPos + increment;
                            } else {
                                xPosToShiftFrom = cursorPos + increment;
                            }
                        } else {
                            movePossible = false;
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

    private void handleMerge(int xFrom, int xTo, int yFrom, int yTo) {
        board[yTo][xTo] *= 2;
        board[yFrom][xFrom] = 0;
        score += board[yTo][xTo];
    }

    private void handleNormalMove(int xFrom, int xTo, int yFrom, int yTo) {
        board[yTo][xTo] = board[yFrom][xFrom];
        board[yFrom][xFrom] = 0;
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
