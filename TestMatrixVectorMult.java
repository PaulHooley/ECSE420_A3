package ECSE420_A3;

import ECSE420_A3.MatrixVectorMult;

public class TestMatrixVectorMult {
    static MatrixVectorMult matrixVectorMult = new MatrixVectorMult();
    public static final int LENGTH = 2000;
    public static final int NUMBER_THREADS = 8;

    public static void main(String[] args){
        System.out.println("Seq Multiplier");
        int[][] m = generateRandomMatrix(LENGTH, LENGTH);
        int[] v = generateRandomVector(LENGTH);
        long st = System.nanoTime();
        matrixVectorMult.SeqMatrixVectorMult(m,v);
        long et = System.nanoTime();
        System.out.println("Seq Multiplier Duration (in nanoseconds): " + (et-st));
        st = System.nanoTime();
        matrixVectorMult.parallelMultiply(m, v, NUMBER_THREADS);
        et = System.nanoTime();
        System.out.println("Parallel Multiplier 1 Duration (in nanoseconds): " + (et-st));
    }

    /**
	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
    private static int[][] generateRandomMatrix (int numRows, int numCols) {
        int matrix[][] = new int[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }
    
    /**
	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
    private static int[] generateRandomVector ( int length) {
        int vector[] = new int[length];
        for (int i = 0 ; i < length ; i++ ) {
            vector[i] = ((int) (Math.random() * 10.0));
        }
        return vector;
    }
}
