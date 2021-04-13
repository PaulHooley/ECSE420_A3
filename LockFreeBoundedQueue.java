package ECSE420_A3;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeBoundedQueue<E> {
    private E[] elements;

    
    private AtomicInteger head = new AtomicInteger(0);
    private AtomicInteger tail = new AtomicInteger(0);
    
    private AtomicInteger tailCommit = new AtomicInteger(0);
    private AtomicInteger capacity;

    public LockFreeBoundedQueue(int capacity){
        this.elements = (E[]) new Object[capacity];
        this.capacity = new AtomicInteger(capacity);
    }

    public void enqueue(E item){
        int tmp = capacity.get();

        // Wait until
        while (tmp <= 0 || !capacity.compareAndSet(tmp, tmp - 1)) {
            tmp = capacity.get();
        }
        int tail = this.tail.getAndIncrement();
        elements[tail] = item;

        // Wait
        while(this.tailCommit.compareAndSet(tail,tail+1)){};

    }

    public E dequeue(){
        // New head index
        int h = this.head.getAndIncrement();
        h = h % elements.length;
        
        // wait until 
        while(h>=tailCommit.get()){};
        
        E item = elements[h];
        capacity.incrementAndGet();

        return item;
    }    
}
