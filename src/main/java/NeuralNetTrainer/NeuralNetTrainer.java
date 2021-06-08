package NeuralNetTrainer;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class NeuralNetTrainer {


    public static double[][][] train(ArrayList<double[][][]> listOfnn ) {
        
        ExecutorService executor = new ThreadPoolExecutor(6,6 ,0L ,TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(listOfnn.size()));


        List<Future<Data>> futureList = new ArrayList<Future<Data>>();
        List<WorkThread> workThreadList = new ArrayList<WorkThread>();

        for (int indexOfnn = 0; indexOfnn < listOfnn.size(); indexOfnn++) {
            workThreadList.add(new WorkThread(listOfnn.get(indexOfnn),indexOfnn));
        }

        // https://www.journaldev.com/1090/java-callable-future-example
        for (WorkThread workThread : workThreadList) {
            futureList.add(executor.submit(workThread));
        }
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        for (Future<Data> future : futureList) {
            try {
                System.out.println( future.get().progress);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Collections.sort(data, new Comparator<Data>() {
        //     @Override
        //     public int compare(Data d1, Data d2) {
        //         // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
        //         return d1.progress > d2.progress ? -1 : (d1.progress < d2.progress) ? 1 : 0;
        //     }
        // });

        // double[][][] TheBestNN = sort op the best fitnes;
        executor.shutdown();    
        return null ; 
    }

    

    
}
