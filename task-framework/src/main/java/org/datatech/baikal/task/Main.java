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

package org.datatech.baikal.task;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.datatech.baikal.task.command.BaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;

/**
 * Entry point class for task execution.
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Main implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private Framework framework;

    /**
     * Main method to start spring boot application.
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        Options options = new Options();
        Option dtCmdTask = new Option("c", "monitorTask", false, "run monitor task");
        dtCmdTask.setRequired(false);
        options.addOption(dtCmdTask);
        Option commandName = new Option("n", "commandName", true, "command name");
        commandName.setRequired(false);
        options.addOption(commandName);
        Option commandParams = new Option("p", "commandParams", true, "command parameters");
        commandParams.setRequired(false);
        options.addOption(commandParams);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Exception occurred.", e);
            formatter.printHelp("datalake-task", options);
            return;
        }
        if (cmd.hasOption("c")) {
            framework.processDtStats();
        } else if (cmd.hasOption("n")) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends BaseCommand> c = (Class<? extends BaseCommand>) Class.forName(
                        String.format("%s.%s", BaseCommand.class.getPackage().getName(), cmd.getOptionValue("n")));
                String params = cmd.getOptionValue("p");
                BaseCommand command = appContext.getBean(c);
                if (command.isEnabled()) {
                    logger.info("execute command [{}], param [{}]", c.getName(), params);
                    command.execute(params);
                } else {
                    logger.warn("command [{}] is disabled", c.getName());
                }
            } catch (ClassNotFoundException ex) {
                logger.warn("command class not found : [{}]", ex.getMessage());
            }
        } else {
            framework.processGeneralTask();
        }

    }
}