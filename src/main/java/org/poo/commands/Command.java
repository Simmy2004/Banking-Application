package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;


public interface Command {
    /**
     * Base for future command specialized executions. Command Pattern.
     * @return ObjectNode containing information requested for every
     * implemented command.
     */
    ObjectNode execute();
}
