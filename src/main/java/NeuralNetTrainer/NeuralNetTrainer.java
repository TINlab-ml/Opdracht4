package NeuralNetTrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NeuralNetTrainer {


    public static double[][][] train(ArrayList<double[][][]> listOfnn,int id ) {
        int programsThread =10;
        
        ExecutorService executor = new ThreadPoolExecutor(programsThread,programsThread,0L ,TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(listOfnn.size()));


        List<Future<Data>> futureList = new ArrayList<Future<Data>>();
        List<WorkThread> workThreadList = new ArrayList<WorkThread>();

        for (int indexOfnn = 0; indexOfnn < listOfnn.size(); indexOfnn++) {
            workThreadList.add(new WorkThread(listOfnn.get(indexOfnn),indexOfnn));
        }

        // https://www.journaldev.com/1090/java-callable-future-example
        for (WorkThread workThread : workThreadList) {
            futureList.add(executor.submit(workThread));
        }
        
        ArrayList<Data> dataArrayList = new ArrayList<Data>();
        for (Future<Data> future : futureList) {
            try {
                dataArrayList.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        
        
        Collections.sort(dataArrayList, new Comparator<Data>() {
            @Override
            public int compare(Data d1, Data d2) {
                return d1.progress > d2.progress ? -1 : (d1.progress < d2.progress) ? 1 : 0;
            }
        });


        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        executor.shutdown();    
        System.out.println("gen: " + id+" the best progress: "+dataArrayList.get(0).progress);
        return dataArrayList.get(0).nn ; 
    }

    

    
}
