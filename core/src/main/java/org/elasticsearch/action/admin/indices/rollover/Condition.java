/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.indices.rollover;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.io.stream.NamedWriteable;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ObjectParser;

import java.util.Set;

/**
 * Base class for rollover request conditions
 */
public abstract class Condition<T> implements NamedWriteable {

    public static ObjectParser<Set<Condition>, Void> PARSER = new ObjectParser<>("conditions", null);
    static {
        PARSER.declareString((conditions, s) ->
            conditions.add(new MaxAgeCondition(TimeValue.parseTimeValue(s, MaxAgeCondition.NAME))),
            new ParseField(MaxAgeCondition.NAME));
        PARSER.declareLong((conditions, value) ->
            conditions.add(new MaxDocsCondition(value)), new ParseField(MaxDocsCondition.NAME));
    }

    protected T value;
    protected final String name;

    protected Condition(String name) {
        this.name = name;
    }

    public abstract Result evaluate(final Stats stats);

    @Override
    public final String toString() {
        return "[" + name + ": " + value + "]";
    }

    /**
     * Holder for index stats used to evaluate conditions
     */
    public static class Stats {
        public final long numDocs;
        public final long indexCreated;

        public Stats(long numDocs, long indexCreated) {
            this.numDocs = numDocs;
            this.indexCreated = indexCreated;
        }
    }

    /**
     * Holder for evaluated condition result
     */
    public static class Result {
        public final Condition condition;
        public final boolean matched;

        protected Result(Condition condition, boolean matched) {
            this.condition = condition;
            this.matched = matched;
        }
    }
}
