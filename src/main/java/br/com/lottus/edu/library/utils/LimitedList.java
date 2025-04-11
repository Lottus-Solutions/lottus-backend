package br.com.lottus.edu.library.utils;

import java.util.ArrayList;

public class LimitedList<E> extends ArrayList<E> {
    private final int maxSize;


    public LimitedList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e){
        if(this.size() >= maxSize){
            throw new IllegalStateException("Tamanho maximo do array atingido" + maxSize);
        }

        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        if(this.size() >= maxSize){
            throw new IllegalStateException(("Tamanho maximo do array atingido" + maxSize));
        }
        super.add(index, element);
    }
}
