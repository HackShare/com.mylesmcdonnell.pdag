package com.mylesmcdonnell.pdag;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by myles on 09/08/2015.
 */
public class ConcurrencyThrottle
{
    private int _maxValue;
    private int _currentValue;

    private final Lock _lock = new ReentrantLock();
    private final Condition _condition = _lock.newCondition();

    public ConcurrencyThrottle() {
        this(0);
    }

    public ConcurrencyThrottle(int maxValue) {
        if (maxValue < 1)
            _maxValue = Runtime.getRuntime().availableProcessors();
        else
            _maxValue = maxValue;
    }

    public void exit() {
        _lock.lock();

        try {
            _currentValue--;
            _condition.notifyAll();
        }
        finally {
            _lock.unlock();
        }
    }

    public void enter() throws InterruptedException {
        _lock.lock();

        try {
            while (_currentValue >= _maxValue) {
                _condition.wait();
            }

            _maxValue = Math.max(++_currentValue, _maxValue);
        }
        finally {
            _lock.unlock();
        }
    }

    public int get_maxValue() {
        return _maxValue;
    }

    public int get_currentValue() {
        return _currentValue;
    }
}