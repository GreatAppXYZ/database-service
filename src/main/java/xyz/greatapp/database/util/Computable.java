package xyz.greatapp.database.util;

public interface Computable<A, V>
{
    V compute(A arg) throws InterruptedException;
}
