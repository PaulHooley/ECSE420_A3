package ECSE420_A3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<E>{

    //Queue array of elements
    private E[] elements;
    private int capacity;

    // Sliding pointers 
    private int head = 0;
    private int tail = 0;

    //Locks
    private Lock lockHead = new ReentrantLock();
    private Lock lockTail = new ReentrantLock();

    // Lock conditions
    private Condition notFull = lockHead.newCondition(); 
    private Condition notEmpty = lockTail.newCondition(); 

    public BoundedQueue(){
        this.elements = (E[]) new Object[capacity];
    }

    public void enqueue(E item){
        lockTail.lock();

        // Wait until queue is not full then unlock
        try{
            while (tail-head == capacity) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {}
            }
            elements[tail] = item;
            tail = tail++ % capacity;

            // Signal lock condition
            if(tail-head == 1){
                notEmpty.signal();
            }
        } finally {
            lockTail.unlock();
        }
    }

    public E dequeue(){
        lockHead.lock();
        // Wait until there are elements in queue then unlock
        try{
            while(tail - head ==0) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {}
            }
            E element = elements[head];
            head = head++ % capacity;

            // Space is still available
            if(tail - head == capacity-1){
                notFull.signal();
            }
            return element;
        }
        finally {
            lockHead.unlock();
        }
    }
}