// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

suite("statistic_table_compaction", "nonConcurrent,p0") {
    def backendId_to_backendIP = [:]
    def backendId_to_backendHttpPort = [:]
    getBackendIpHttpPort(backendId_to_backendIP, backendId_to_backendHttpPort);

    def do_compaction = { String table ->
        try {
            def tablets = sql_return_maparray """show tablets from ${table}"""

            // trigger compactions for all tablets in ${tableName}
            trigger_and_wait_compaction(table, "full")
        } catch (Exception e) {
            logger.info(e.getMessage())
            if (e.getMessage().contains("Unknown table")) {
                return
            } else {
                throw e
            }
        }
    }

    do_compaction("__internal_schema.column_statistics")
    do_compaction("__internal_schema.partition_statistics")
}
