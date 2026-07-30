package com.koreanre.ifrs17.businessservice.domain.csm;

import com.koreanre.ifrs17.businessservice.api.dto.response.StatusServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * IFRS17.CSM.STATUS 응답 DTO (설계서 9.5).
 */
public class CsmStatusResponse extends StatusServiceResponse {

    private long totalContractCount;
    private List<CsmPortfolioStatus> portfolios = new ArrayList<CsmPortfolioStatus>();

    public long getTotalContractCount() {
        return totalContractCount;
    }

    public void setTotalContractCount(long totalContractCount) {
        this.totalContractCount = totalContractCount;
    }

    public List<CsmPortfolioStatus> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<CsmPortfolioStatus> portfolios) {
        this.portfolios = (portfolios == null) ? new ArrayList<CsmPortfolioStatus>() : portfolios;
    }
}
