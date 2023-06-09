package com.somoff.telegrambotdemoproject.service;

import com.somoff.telegrambotdemoproject.exception.ServiceException;

import java.math.BigDecimal;

public interface ExchangeRates {
    BigDecimal getUSDExchangeRate() throws ServiceException;

    BigDecimal getEURExchangeRate() throws ServiceException;

    BigDecimal getCNYExchangeRate() throws ServiceException;
}
