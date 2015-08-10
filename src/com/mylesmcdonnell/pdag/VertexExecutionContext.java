package com.mylesmcdonnell.pdag;

/**
 * Created by myles on 10/08/2015.
 */
public abstract class VertexExecutionContext {

    void run(VertexExecutionScheduler executionScheduler, Vertex vertex)  {
        boolean failure = false;
        try {
            Execute();
        }
        catch (Exception exception){
            failure = true;
            executionScheduler.process_post_vertex_execution_failure(vertex, exception);
        }

        if (!failure)
            executionScheduler.process_post_vertex_execution_success(vertex);
    }

    abstract void Execute() throws Exception;
}
