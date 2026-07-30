package com.koreanre.ifrs17.businessservice.domain.statement;

import com.koreanre.ifrs17.businessservice.api.dto.response.StatusServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * IFRS17.STATEMENT.STATUS 응답 DTO (설계서 9.4).
 */
public class StatementStatusResponse extends StatusServiceResponse {

    private List<StatementTypeStatus> statements = new ArrayList<StatementTypeStatus>();

    public List<StatementTypeStatus> getStatements() {
        return statements;
    }

    public void setStatements(List<StatementTypeStatus> statements) {
        this.statements = (statements == null) ? new ArrayList<StatementTypeStatus>() : statements;
    }
}
