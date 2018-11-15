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
package org.datatech.baikal.task.command;

/**
 * Defines interface for command classes which invoked by command line.
 */
public abstract class BaseCommand {
    /**
     * Execute the with the parameter provided in the command line.
     *
     * @param params Command line parameters.
     * @throws Exception Throws in case or error.
     */
    public abstract void execute(String params) throws Exception;

    /**
     * Checks whether this command is enabled or not.
     *
     * @return true if enabled, false otherwise.
     */
    public abstract boolean isEnabled();
}
