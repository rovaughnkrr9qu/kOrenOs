package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import java.util.ArrayList;

/**
 * Created by Markos on 18. 11. 2016.
 */

public class Statistics {

    private ArrayList<Double> dataContainer;
    private int maxItemsCount = 0;
    private int iterator = 0;
    private boolean stackFull = false;
    private int maxChange = 0;

    public Statistics(int maxItemsCount, int maxChange) {
        this.maxItemsCount = maxItemsCount;
        this.maxChange = maxChange;
        dataContainer = new ArrayList<>(maxItemsCount+5);
    }

    public void add(double value){
        checkBounds(value);

        if(stackFull){
            dataContainer.set(iterator, value);
        }else {
            dataContainer.add(value);
        }
        iterator++;

        if(iterator == maxItemsCount){
            iterator = 0;
            stackFull = true;
        }
    }

    private void checkBounds(double value){
        if(!dataContainer.isEmpty()){
            int temp = iterator-1;
            if(temp < 0) {
                temp = maxItemsCount -1;
            }
            if(Math.abs(dataContainer.get(temp) - value) > maxChange){
                dataContainer.clear();
                iterator = 0;
                stackFull = false;
            }
        }
    }

    public double getAverage(){
        double sum = 0;
        for(Double value : dataContainer){
            sum += value;
        }

        return sum/dataContainer.size();
    }

    public boolean dataGathered(){
        return stackFull;
    }
}
