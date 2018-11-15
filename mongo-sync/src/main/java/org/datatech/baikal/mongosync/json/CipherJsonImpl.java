/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datatech.baikal.mongosync.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serialize object to json fasterxml implementation.
 */
public class CipherJsonImpl implements JsonInterface {
    private static final Logger logger = LoggerFactory.getLogger(CipherJsonImpl.class);
    private final ObjectMapper mapper;
    private final String nstring = "";

    public CipherJsonImpl() {
        mapper = new ObjectMapper();
    }

    @Override
    public String object2JsonString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error("Exception occurred.", e);
        }
        return nstring;
    }
}
