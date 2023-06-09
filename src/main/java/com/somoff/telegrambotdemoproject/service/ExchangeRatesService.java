package com.somoff.telegrambotdemoproject.service;

import com.somoff.telegrambotdemoproject.client.CbrClient;
import com.somoff.telegrambotdemoproject.exception.ServiceException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExchangeRatesService implements ExchangeRates {

    private final CbrClient cbrClient;

    @Autowired
    public ExchangeRatesService(CbrClient cbrClient) {
        this.cbrClient = cbrClient;
    }

    @Override
    public BigDecimal getUSDExchangeRate() throws ServiceException {
        return convertCurrencyRateFromJson().getJSONObject("USD").getBigDecimal("Value");
    }

    @Override
    public BigDecimal getEURExchangeRate() throws ServiceException {
        return convertCurrencyRateFromJson().getJSONObject("EUR").getBigDecimal("Value");
    }

    @Override
    public BigDecimal getCNYExchangeRate() throws ServiceException {
        return convertCurrencyRateFromJson().getJSONObject("CNY").getBigDecimal("Value");
    }

    private JSONObject convertCurrencyRateFromJson() throws ServiceException {
        return new JSONObject(cbrClient.getCurrencyRates())
                .getJSONObject("Valute");


    }
}
