package com.example.bookingapp;

public class ParsingServerDataException extends Exception{
   public ParsingServerDataException(String errorMessage,Throwable err){
       super(errorMessage,err);
   }
}
