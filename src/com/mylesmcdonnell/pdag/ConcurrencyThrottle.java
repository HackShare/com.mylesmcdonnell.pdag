//   Copyright 2015 Myles McDonnell (mcdonnell.myles@gmail.com)

//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mylesmcdonnell.pdag;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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