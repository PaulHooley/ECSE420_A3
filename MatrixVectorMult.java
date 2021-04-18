package ECSE420_A3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/*
*  Inspired by:https://github.com/stumash/ParallelComputingExercises/blob/master/03/code/src/main/java/ca/mcgill/ecse420/a3/MatrixMultiplication/ParallelMultiplier_Practical.java 
*/
public class MatrixVectorMult {

    // Sequential matrix vector multiplyer, O(n^2)
    public int[] SeqMatrixVectorMult(int[][] m, int[] v){
        int[] out = new int[v.length];
        for(int i = 0; i < m.length; i++){
            out[i] = 0;
            for(int j = 0; j < out.length; j++){
                out[i] += m[i][j] * v[j];
            } 
        }        
        return out;
    }


    public int[] parallelMult(int[][] matrix, int[] vector, int number_threads)
    {
        int[] out = new int[matrix.length];
        List<Callable<Object>> tasks = getTasks(number_threads, matrix, vector, out);
        ExecutorService threadPool = Executors.newFixedThreadPool(number_threads);
        try {
            threadPool.invokeAll(tasks);
            threadPool.shutdown();
        } catch(Exception e){}

        return out;
    }

    private List<Callable<Object>> getTasks(int number_threads, int[][] matrix, int[] vector, int[] out) {
        double rowsPerThread  = matrix.length / (double)number_threads;
        int lowRowsPerThread  = (int)Math.floor(rowsPerThread);
        int highRowsPerThread = (int)Math.ceil(rowsPerThread);

        double fracRowsPerThreadHighToLow = rowsPerThread % 1;
        int numThreadsHighRowsPerThread   = (int)(fracRowsPerThreadHighToLow * number_threads);
        int numThreadsLowRowsPerThread    = number_threads-1-numThreadsHighRowsPerThread;

        if (fracRowsPerThreadHighToLow == 0.0) {
            numThreadsHighRowsPerThread = number_threads;
            numThreadsLowRowsPerThread  = 0;
        }

        List<Callable<Object>> tasks = new ArrayList<>();

        int rowRangeStart = 0;
        int rowRangeEnd = 0;
        for (int threadNum = 0; threadNum < number_threads; threadNum++) {
            if (threadNum < numThreadsHighRowsPerThread) {
                rowRangeEnd = rowRangeStart + highRowsPerThread;
            } else if (threadNum < numThreadsHighRowsPerThread + numThreadsLowRowsPerThread) {
                rowRangeEnd = rowRangeStart + lowRowsPerThread;
            } else {
                rowRangeEnd = matrix.length;
            }

            PartialMatrixVectorMultiplication p =
                    new PartialMatrixVectorMultiplication(matrix, vector, out, rowRangeStart,rowRangeEnd);
            rowRangeStart = rowRangeEnd;

            tasks.add(Executors.callable(p));
        }

        return tasks;
    }

    class PartialMatrixVectorMultiplication implements Runnable{
        private int[][] matrix;
        private int[] vector, out;
        private int rowRangeStart, rowRangeEnd;
        PartialMatrixVectorMultiplication(int[][] matrix, int[] vector, int[] out, int rowRangeStart, int rowRangeEnd) {
            this.matrix = matrix;
            this.vector = vector;
            this.out = out;
            this.rowRangeStart = rowRangeStart;
            this.rowRangeEnd = rowRangeEnd;
        }
        @Override
        public void run() {
            for (int rowIdx = rowRangeStart; rowIdx < rowRangeEnd; rowIdx++) {
                int[] row = matrix[rowIdx];
                int dotProduct = 0;

                for (int i = 0; i < matrix.length; i++) {
                    dotProduct += row[i] * vector[i];
                }
                out[rowIdx] = dotProduct;
            }
        }
        
    }
}
