package spw4.game2048;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {
    static class RandomStub extends Random {
        public RandomStub(int[] returnVals) {
            this.returnVals = returnVals;
        }

        public RandomStub() {
        }

        int[] returnVals = new int[]{1, 1, 0, 1, 2, 1, 0, 0, 0};
        int counter = 0;

        @Override
        public int nextInt(int bound) {
            return returnVals[counter++];
        }
    }

    @BeforeEach
    void setupTest() {
        GameImpl.random = new Random();
    }

    @DisplayName("Init")
    @Nested
    class InitTests {
        @Test
        void createBoardReturns4By4Field() {
            var sut = new GameImpl();
            sut.initialize();
            var expectedSize = 4;

            int boardSize = sut.getBoardSize();

            assertEquals(expectedSize, boardSize);
        }

        @Test
        void createBoardReturnsBoardWithSum4or6() {
            var sut = new GameImpl();
            sut.initialize();
            int[] expected = new int[]{4, 6, 8};

            int sum = getBoardSum(sut);

            assertThat(expected).contains(sum);
        }

        @Test
        void createBoardUsesRandom() {
            GameImpl.random = spy(Random.class);
            var sut = new GameImpl();

            sut.initialize();

            verify(GameImpl.random);
        }

        /**
         * Creates this board:
         * <p>
         * 0 | 0 | 0 | 0
         * 0 | 4 | 2 | 0
         * 0 | 0 | 0 | 0
         * 0 | 0 | 0 | 0
         */
        @Test
        void createBoardUsingRandomStubReturnsBoardWithOne4One2() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expectedSum = 6;
            var expectedFirst = 4;
            var expectedSecond = 2;

            assertEquals(expectedSum, getBoardSum(sut));
            assertEquals(expectedFirst, sut.getValueAt(1, 1));
            assertEquals(expectedSecond, sut.getValueAt(2, 1));
        }

        @Test
        void createBoardReturnsNotWonYet() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expected = false;

            assertEquals(expected, sut.isWon());
        }

        @Test
        void createBoardReturnsNotGameOverYet() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expected = false;

            assertEquals(expected, sut.isOver());
        }

        @Test
        void isOverWithFullBoardNoMovesLeftReturnsTrue() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            for (int i = 0; i < sut.getBoardSize(); i++) {
                for (int k = 0; k < sut.getBoardSize(); k++) {
                        sut.placeTile(i, k, ((i + k) % 2 == 0) ? 4 : 2);
                }
            }
            var expected = true;

            assertEquals(expected, sut.isOver());
        }

        @Test
        void isOverWithFullBoardOneMoveLeftReturnsFalse() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            for (int i = 0; i < sut.getBoardSize(); i++) {
                for (int k = 0; k < sut.getBoardSize(); k++) {
                    sut.placeTile(i, k, ((i + k) % 2 == 0) ? 4 : 2);
                }
            }
            sut.placeTile(0, 0, 2);
            var expected = false;

            assertEquals(expected, sut.isOver());
        }

        @Test
        void isWonWithSingle2048ReturnsTrue() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.placeTile(0, 0, 2048);
            var expected = true;

            assertEquals(expected, sut.isWon());
        }

        private int getBoardSum(GameImpl sut) {
            int sum = 0;
            for (int i = 0; i < sut.getBoardSize(); i++) {
                for (int o = 0; o < sut.getBoardSize(); o++) {
                    sum += sut.getValueAt(i, o);
                }
            }
            return sum;
        }
    }


    @DisplayName("Move")
    @Nested
    class MoveTests {
        @Test
        void moveRightMovesTilesToRight() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 2;

            sut.move(Direction.right);

            assertEquals(expectedFirst, sut.getValueAt(sut.getBoardSize() - 2, 1));
            assertEquals(expectedSecond, sut.getValueAt(sut.getBoardSize() - 1, 1));
        }

        @Test
        void moveLeftMovesTilesToLeft() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 2;

            sut.move(Direction.left);

            assertEquals(expectedFirst, sut.getValueAt(0, 1));
            assertEquals(expectedSecond, sut.getValueAt(1, 1));
        }

        @Test
        void moveUpMovesTilesUp() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 2;

            sut.move(Direction.up);

            assertEquals(expectedFirst, sut.getValueAt(1, 0));
            assertEquals(expectedSecond, sut.getValueAt(2, 0));
        }

        @Test
        void moveDownMovesTilesDown() {
            GameImpl.random = new RandomStub();
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 2;

            sut.move(Direction.down);

            assertEquals(expectedFirst, sut.getValueAt(1, sut.getBoardSize() - 1));
            assertEquals(expectedSecond, sut.getValueAt(2, sut.getBoardSize() - 1));
        }

        @Test
        void moveCreatesRandomTile() {
            GameImpl.random = new RandomStub(new int[]{1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 3, 1});
            var sut = spy(GameImpl.class);
            sut.initialize();

            sut.move(Direction.down);

            verify(sut, times(3)).placeRandomTile();
        }

        @Test
        void moveOnceGetMovesReturnsCount(){
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 1;

            sut.move(Direction.down);

            assertEquals(expected, sut.getMoves());
        }

        @Test
        void moveTwiceSameDirectionGetMovesReturnsCount(){
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 1;

            sut.move(Direction.up);
            sut.move(Direction.up);

            assertEquals(expected, sut.getMoves());
        }

        @Test
        void moveTwiceDifferentDirectionsGetMovesReturnsCount(){
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 2, 1, 0, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 2;

            sut.move(Direction.down);
            sut.move(Direction.up);

            assertEquals(expected, sut.getMoves());
        }

        @Test
        void moveOnceNoTileMovedGetMovesReturnsCount(){
            GameImpl.random = new RandomStub(new int[]{0, 0, 1, 1, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 0;

            sut.move(Direction.up);

            assertEquals(expected, sut.getMoves());
        }
    }

    @DisplayName("Add")
    @Nested
    class AddTests {
        @Test
        void moveLeftTwo2InMiddleAddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 1, 1, 1, 2, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 4;

            sut.move(Direction.left);

            assertEquals(expected, sut.getValueAt(0, 1));
        }

        @Test
        void moveRightTwo2InMiddleAddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 1, 1, 1, 2, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 4;

            sut.move(Direction.right);

            assertEquals(expected, sut.getValueAt(sut.getBoardSize() - 1, 1));
        }

        @Test
        void moveRightTwo2OnRightAddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 2, 1, 1, 3, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 4;

            sut.move(Direction.right);

            assertEquals(expected, sut.getValueAt(sut.getBoardSize() - 1, 1));
        }

        @Test
        void moveLeftTwo2OnRightAddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 2, 1, 1, 3, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 4;

            sut.move(Direction.left);

            assertEquals(expected, sut.getValueAt(0, 1));
        }

        @Test
        void moveLeftTwo2OnLeftAddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 0, 1, 1, 1, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expected = 4;

            sut.move(Direction.left);

            assertEquals(expected, sut.getValueAt(0, 1));
        }

        @Test
        void moveLeft4TwosAddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 3, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 4;

            sut.placeRandomTile();
            sut.placeRandomTile();
            sut.move(Direction.left);

            assertEquals(expectedFirst, sut.getValueAt(0, 1));
            assertEquals(expectedSecond, sut.getValueAt(1, 1));
        }

        @Test
        void moveLeft2TwosOne4AddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{1, 0, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 4;

            sut.placeRandomTile();
            sut.move(Direction.left);

            assertEquals(expectedFirst, sut.getValueAt(0, 1));
            assertEquals(expectedSecond, sut.getValueAt(1, 1));
        }

        @Test
        void moveUp2TwosOne4AddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 4;

            sut.placeRandomTile();
            sut.move(Direction.up);

            assertEquals(expectedFirst, sut.getValueAt(1, 0));
            assertEquals(expectedSecond, sut.getValueAt(1, 1));
        }

        @Test
        void moveDown2TwosOne4AddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expectedFirst = 4;
            var expectedSecond = 4;

            sut.placeRandomTile();
            sut.move(Direction.down);

            assertEquals(expectedFirst, sut.getValueAt(1, sut.getBoardSize() - 2));
            assertEquals(expectedSecond, sut.getValueAt(1, sut.getBoardSize() - 1));
        }
    }

    @DisplayName("Score")
    @Nested
    class ScoreTests {
        @Test
        void moveDown2TwosOne4AddsTilesTogether() {
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expectedScore = 4;

            sut.move(Direction.down);

            assertEquals(expectedScore, sut.getScore());
        }

        @Test
        void moveMultipleTimesAddsToScore() {
            GameImpl.random = new RandomStub(new int[]{0, 1, 1, 1, 1, 1, 3, 2, 0, 0, 0, 0});
            var sut = new GameImpl();
            sut.initialize();
            var expectedScore = 12;

            sut.move(Direction.down);
            sut.move(Direction.left);

            assertEquals(expectedScore, sut.getScore());
        }
    }

    @Test
    void toStringOnEmptyField(){
        var sut = new GameImpl();
        var expected = """
                Moves: 0\t\tScore: 0
                .    .    .    .
                .    .    .    .
                .    .    .    .
                .    .    .    .""";

        assertEquals(expected, sut.toString());
    }

    @Test
    void toStringOnInitializedField(){
        GameImpl.random = new RandomStub();
        var sut = new GameImpl();
        sut.initialize();
        var expected = """
                Moves: 0\t\tScore: 0
                .    .    .    .
                .    4    2    .
                .    .    .    .
                .    .    .    .""";

        assertEquals(expected, sut.toString());
    }

    @Test
    void toStringOnInitializedFieldWithBigNumber(){
        GameImpl.random = new RandomStub();
        var sut = new GameImpl();
        sut.initialize();
        sut.placeTile(0,0,2048);
        var expected = """
                Moves: 0\t\tScore: 0
                2048 .    .    .
                .    4    2    .
                .    .    .    .
                .    .    .    .""";

        assertEquals(expected, sut.toString());
    }

    @Test
    void toStringAfterOneMove(){
        GameImpl.random = new RandomStub(new int[]{0,0,0 , 2,2,1, 1,1,0, 0,0,0});
        var sut = new GameImpl();
        sut.initialize();
        var expected = """
                Moves: 0\t\tScore: 0
                4    .    .    .
                .    .    .    .
                .    .    2    .
                .    .    .    .""";

        assertEquals(expected, sut.toString());


        expected = """
                Moves: 1\t\tScore: 0
                .    .    .    .
                .    4    .    .
                .    .    .    .
                4    .    2    .""";

        sut.move(Direction.down);

        assertEquals(expected, sut.toString());
    }
}