/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.sjms.it;

import java.util.UUID;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.apache.camel.quarkus.core.CamelMain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(CamelSjmsTestResource.class)
public class CamelSjmsTest {
    @Inject
    CamelMain main;

    @Test
    void testQueueBridge() {
        String body = UUID.randomUUID().toString();

        RestAssured.given()
                .contentType("text/plain")
                .body(body)
                .post("/test/jms/inbound")
                .then()
                .statusCode(200);

        JsonPath result = RestAssured.given()
                .get("/test/jms/outbound")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        assertThat(result.getString("queueName")).isEqualTo("outbound");
        assertThat(result.getString("body")).isEqualTo(body);
    }
}
