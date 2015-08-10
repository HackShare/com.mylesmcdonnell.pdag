package com.mylesmcdonnell.pdag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by myles on 09/08/2015.
 */
public class GraphExecutive implements VertexExecutionScheduler {

    private DirectedAcyclicGraph _graph;
    private ArrayList<Vertex> _verticesComplete, _verticesScheduled;
    private int _verticesProcessed, _allVerticesCount;

    private Dictionary<Vertex, Exception> _verticesFailed;
    private ExecutorService _executorService;

    public GraphExecutive(DirectedAcyclicGraph graph, int maxConcurrency) {
        if (maxConcurrency<1)
            _executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        else
            _executorService = Executors.newFixedThreadPool(maxConcurrency);

        _graph = graph;
    }

    public synchronized void execute_and_wait() throws InterruptedException
    {
        Execute();
        wait();
    }

    public synchronized void Execute()
    {
        _verticesComplete = new ArrayList<>();
        _verticesScheduled = new ArrayList<>();
        _verticesFailed = new Hashtable<>();
        _verticesProcessed = 0;
        _allVerticesCount = _graph.get_all_vertices().length;

        for (Vertex vertex : _graph.get_root_vertices())
            execute(vertex);
    }

    @Override
    public synchronized void process_post_vertex_execution_success(Vertex vertex)
    {
        _verticesComplete.add(vertex);
        _verticesProcessed++;

        if (_verticesProcessed >= _allVerticesCount) {
            notify();
            return;
        }

        Collection<Vertex> next = new ArrayList<>();

        for(Vertex dependent : vertex.get_dependents())
            if (!_verticesScheduled.contains(dependent) && _verticesComplete.containsAll(dependent.get_dependencies()))
                next.add(dependent);

        next.forEach(this::execute);
    }

    @Override
    public synchronized void process_post_vertex_execution_failure(Vertex vertex, Exception exception)
    {
        _verticesComplete.add(vertex);
        _verticesFailed.put(vertex, exception);
        _verticesProcessed++;

        for (Vertex dependent : Extensions.flatten_and_get_distinct(vertex.get_dependents().toArray(new Vertex[0])))
        {
            _verticesComplete.add(dependent);
            _verticesFailed.put(dependent, new Exception("Dependency Failed"));
            _verticesProcessed++;
        }

        if (_verticesProcessed >= _allVerticesCount)
            notify();
    }

    private void execute(Vertex vertex) {
        _verticesScheduled.add(vertex);
        _executorService.submit(new VertexExecutionContext(vertex, this));
    }

    public Dictionary<Vertex, Exception> get_vertices_failed() {
        return _verticesFailed;
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
            boolean failure = false;
            try {
                _vertex.run();
            }
            catch (Exception exception){
                failure = true;
                _executive.process_post_vertex_execution_failure(_vertex, exception);
            }

            if (!failure)
                _executive.process_post_vertex_execution_success(_vertex);
        }
    }
}

