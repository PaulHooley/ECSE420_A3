package ECSE420_A3;

import java.util.concurrent.*;

import jdk.jfr.Threshold;

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

    // Parallel Matrix vector multiplyer
    
    
    public int[] parallelMultiply(int[][] matrix, int[] vector, int number_threads){
        ExecutorService executor = Executors.newFixedThreadPool(number_threads);
        int threshold = matrix.length/(int)(Math.log(number_threads)/Math.log(4)*4);
        System.out.println(threshold);
        int[] res= new int[matrix.length];
        Future f1 = executor.submit(new Multiply(matrix, vector, res, 0, 0, 0, 0, matrix.length, threshold));
        try{
            f1.get();
            executor.shutdown();
        } catch (Exception e){

        }
        return res;
    }

    class Multiply implements Runnable {


        private int[][] matrix;
        private int[] vector, res;
        private int mat_row, mat_col, vec_row, res_row, size, threshold;


        Multiply(int[][] matrix, int[] vector, int[] res, int mat_row, int mat_col, int vec_row, int res_row, int size, int threshold) {
            this.matrix = matrix;
            this.vector = vector;
            this.res = res;

            this.mat_row = mat_row;
            this.mat_col = mat_col;
            this.vec_row = vec_row;
            this.res_row = res_row;
            this.size = size;
            this.threshold = threshold;
        }

        public void run() {
            int half = size / 2;
            if (size < threshold) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        res[res_row + i] += matrix[mat_row + i][mat_col + j] * vector[vec_row + j];
                    }
                }
            } else {
                Multiply[] todo = {
                        new Multiply(matrix, vector, res, mat_row, mat_col, vec_row, res_row, half, threshold),
                        new Multiply(matrix, vector, res, mat_row, mat_col + half, vec_row + half, res_row, half, threshold),
                        new Multiply(matrix, vector, res, mat_row + half, mat_col, vec_row,res_row + half, half, threshold),
                        new Multiply(matrix, vector, res, mat_row + half, mat_col + half, vec_row + half,res_row + half, half, threshold)

                };
                FutureTask[] fs1 = new FutureTask[2];
                fs1[0] = new FutureTask(new HelperSeq(todo[0],todo[1]),null);
                fs1[1] = new FutureTask(new HelperSeq(todo[2],todo[3]),null);
                for (int i = 0; i < fs1.length; ++i) {
                    fs1[i].run();
                }
            }

        }

    }
    static class HelperSeq implements Runnable{


        private Multiply m1, m2;
        HelperSeq(Multiply m1, Multiply m2){
            this.m1 = m1;
            this.m2 = m2;
        }
        public void run(){
            m1.run();
            m2.run();
        }

    }
    


    class FibTask implements Callable<Integer> {
        ExecutorService exec = Executors.newCachedThreadPool();
        int arg;

        public FibTask(int n) {
            arg = n;
        }

        public Integer call() throws InterruptedException, ExecutionException {
            if (arg > 2) {
                Future<Integer> left = exec.submit(new FibTask(arg-1));
                Future<Integer> right = exec.submit(new FibTask(arg-2));
                return left.get() + right.get();
            } else {
                return 1;
            }
        }
    }

    
}
