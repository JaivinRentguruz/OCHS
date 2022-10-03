package com.ochs.app.Card;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CardDetail {

    public static List<CardChecking> cardCheckingList;// = new ArrayList<>();

    public static List<CardChecking> cardList(){
        cardCheckingList = new ArrayList<>();
        cardCheckingList.add(new CardChecking("^4\\d*", "VISA"));
        return cardCheckingList;
    }


    public static CardChecking getCard(String number){

     //   cardCheckingList.add(new CardChecking("^4\\d*", "VISA"));
        cardList();
        Log.e("TAG", "run: "+  cardList().size());

        CardChecking cardChecking = new CardChecking();


        for (int i = 0; i <cardList().size(); i++) {
            if (number.matches(cardList().get(i).prefix)){
                cardChecking = cardList().get(i);
                return cardChecking;

            }
        }

       return null;
    }

}
