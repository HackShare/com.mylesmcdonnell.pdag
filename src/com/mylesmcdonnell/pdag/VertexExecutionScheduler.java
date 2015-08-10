package com.mylesmcdonnell.pdag;

/**
 * Created by myles on 10/08/2015.
 */
public interface VertexExecutionScheduler {
    void process_post_vertex_execution_success(Vertex vertex);

    void process_post_vertex_execution_failure(Vertex vertex, Exception exception);
}
