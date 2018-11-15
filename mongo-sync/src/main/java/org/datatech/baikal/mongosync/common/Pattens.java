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

package org.datatech.baikal.mongosync.common;

import java.util.regex.Pattern;

/**
 * Defines patterns being used.
 */
public class Pattens {
    public static Pattern tsPattern = Pattern.compile("(?i)ts\\d*");
    public static Pattern numberPattern = Pattern.compile("[^0-9]");
    public static Pattern NUMBER_PATTERN = Pattern.compile("\\d*");

}
