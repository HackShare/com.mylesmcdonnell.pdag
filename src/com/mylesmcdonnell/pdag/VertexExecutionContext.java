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
