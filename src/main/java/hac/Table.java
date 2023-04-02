package hac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {

    //private member
    private int NumOfPoll;//num of answer of poll
    private ArrayList<String> str;//the answer of poll
    private List<Integer> resultOfPoll;//result of poll

    //default constructor
    public  Table (){}


    //this function get the answer of user and upload the arr of result
    public synchronized void  setResult(String result) {
        int index = Integer.parseInt(result);
        int saveIndex=resultOfPoll.get(index);
        resultOfPoll.set(index, (saveIndex+1));
    }

    public ArrayList<String> getResultOfPoll() {return str;}

    public List<Integer> getResultOfUser() {return resultOfPoll;}

    public void setResultOfUser( )
    {
        List<Integer> temp =  Collections.synchronizedList(new ArrayList<>(NumOfPoll));
        for(int i =0;i<NumOfPoll ; i++)
            temp.add(0);
        resultOfPoll=temp;
    }

    public void setResultOfPoll( ArrayList<String> strResult) {
        str=strResult;
        NumOfPoll=strResult.size();
    }


}
