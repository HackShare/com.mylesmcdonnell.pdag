package com.mylesmcdonnell.pdag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by myles on 09/08/2015.
 */
public class GraphExecutive {

    private DirectedAcyclicGraph _graph;
    private ArrayList<Vertex> _verticesComplete;
    private int _verticesProcessed, _allVerticesCount;

    private Dictionary<Vertex, Exception> _verticesFailed;
    private ExecutorService _executorService;
    private final ReentrantLock _executionLock = new ReentrantLock();
    private final ReentrantLock _scheduleLock = new ReentrantLock();

    /// <summary>
    /// Executes a graph concurrently.
    /// </summary>
    /// <param name="graph">The graph to execute</param>
    /// <param name="throttle">optional concurrency throttle</param>
    /// <param name="concurrencyThrottleStrategy">optional ConcurrencyThrottleStrategy</param>
    public GraphExecutive(DirectedAcyclicGraph graph, int maxConcurrency) {
        if (maxConcurrency<1)
            _executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        else
            _executorService = Executors.newFixedThreadPool(maxConcurrency);

        _graph = graph;
    }

    /// <summary>
    /// Executes the graph and returns once execution is complete.
    /// </summary>
    public synchronized void execute_and_wait() throws InterruptedException
    {
        _executionLock.lock();

        try {
            Execute();
            wait();
        }
        finally {
            _executionLock.unlock();
        }
    }

    /// <summary>
    /// Executes the graph and returns immediately, unless ConcurrencyThrottleStrategy.PreThreadQueue strategy is specified in which case it will return once all vertices are scheduled
    /// </summary>
    public void Execute()
    {
        _executionLock.lock();

        try {
            _verticesComplete = new ArrayList<Vertex>();
            _verticesFailed = new Hashtable<Vertex, Exception>();
            _verticesProcessed = 0;
            _allVerticesCount = _graph.get_all_vertices().length;
            for (Vertex vertex : _graph.get_root_vertices())
                execute(vertex);

        }
        finally {
            _executionLock.unlock();
        }
    }

    private synchronized void processPostVertexExecutionSuccess(Vertex vertex)
    {
        Collection<Vertex> next = new ArrayList<Vertex>();
        _scheduleLock.lock();

        try {
            _verticesComplete.add(vertex);
            _verticesProcessed++;

            next.addAll(
                    vertex.get_dependents().stream().filter(
                            dependency -> _verticesComplete.containsAll(vertex.get_dependencies()) && !_verticesComplete.contains(dependency)).collect(Collectors.toList()));

            if (_verticesProcessed == _allVerticesCount)
               notify();

            next.forEach(this::execute);
        }
        finally {
            _scheduleLock.unlock();
        }
    }

    private void processPostVertexExecutionFailure(Vertex vertex, Exception exception)
    {
        _scheduleLock.lock();

        try {
            _verticesComplete.add(vertex);
            _verticesFailed.put(vertex, exception);
            _verticesProcessed++;

            for (Vertex dependent : Extensions.flatten_and_get_distinct(vertex.get_dependents().toArray(new Vertex[0])))
            {
                _verticesComplete.add(dependent);
                _verticesFailed.put(dependent, new Exception("Dependency Failed"));
                _verticesProcessed++;
            }
        }
        finally {
            _scheduleLock.unlock();
        }
    }

    private void execute(Vertex vertex) {
        _executorService.submit(new VertexExecutionContext(vertex, this));
    }

    private class VertexExecutionContext implements Runnable {

        private Vertex _vertex;
        private GraphExecutive _executive;

        public VertexExecutionContext(Vertex vertex, GraphExecutive executive) {
            _vertex = vertex;
            _executive = executive;
        }

        @Override
        public void run() {
            try {
                _vertex.run();
                _executive.processPostVertexExecutionSuccess(_vertex);
            }
            catch (Exception exception){
                _executive.processPostVertexExecutionFailure(_vertex, exception);
            }
        }
    }
}

