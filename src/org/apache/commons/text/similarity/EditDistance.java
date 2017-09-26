package org.apache.commons.text.similarity;

/**
 Part of the Apache text similarity software that looks like it should be in
 https://commons.apache.org/proper/commons-text/jacoco/org.apache.commons.text.similarity/EditDistance.java.html
 but isn't.
 Implemented based on the JavaDocs at https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/similarity/EditDistance.html
 <p/>
 Presumably licensed as follows:
 *<blockquote>Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *<blockquote>
 *      http://www.apache.org/licenses/LICENSE-2.0
 *</blockquote>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.</blockquote>
 */

public interface EditDistance<R> {

    R apply( CharSequence left, CharSequence right );

}
