package com.somoff.telegrambotdemoproject.client;

import com.somoff.telegrambotdemoproject.exception.ServiceException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CbrClient {

    private final OkHttpClient okHttpClient;
    @Value("${cbr.currency.rates.json.url}")
    private String URL;

    @Autowired
    public CbrClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public String getCurrencyRates() throws ServiceException {
        Request request = new Request.Builder().url(URL).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            var body = response.body();
            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new ServiceException("Ошибка получения данных о курсе валют", e);
        }
    }
}
