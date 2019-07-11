package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.generalmodels.ClientLine;
import it.polito.ai.project.generalmodels.JsonLine;

import java.net.UnknownServiceException;
import java.util.Collection;

public interface DatabaseServiceInterface{
    void insertLine(JsonLine line) throws InternalServerErrorException;
    Collection<String> getLinesNames() throws UnknownServiceException;
    Collection<ClientLine> getLines() throws UnknownServiceException;
}
