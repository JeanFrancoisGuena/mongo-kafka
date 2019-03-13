/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Original Work: Apache License, Version 2.0, Copyright 2017 Hans-Peter Grahsl.
 */

package com.mongodb.kafka.connect.processor;

import java.util.Set;
import java.util.function.Predicate;

import org.apache.kafka.connect.sink.SinkRecord;

import com.mongodb.kafka.connect.MongoSinkConnectorConfig;
import com.mongodb.kafka.connect.converter.SinkDocument;
import com.mongodb.kafka.connect.processor.field.projection.WhitelistProjector;

public class WhitelistValueProjector extends WhitelistProjector {

    private Predicate<MongoSinkConnectorConfig> predicate;

    public WhitelistValueProjector(final MongoSinkConnectorConfig config, final String collection) {
        this(config, config.getValueProjectionList(collection),
                cfg -> cfg.isUsingWhitelistValueProjection(collection), collection);
    }

    public WhitelistValueProjector(final MongoSinkConnectorConfig config, final Set<String> fields,
                                   final Predicate<MongoSinkConnectorConfig> predicate, final String collection) {
        super(config, fields, collection);
        this.predicate = predicate;
    }

    @Override
    public void process(final SinkDocument doc, final SinkRecord orig) {
        if (predicate.test(getConfig())) {
            doc.getValueDoc().ifPresent(vd -> doProjection("", vd));
        }

        getNext().ifPresent(pp -> pp.process(doc, orig));
    }

}
