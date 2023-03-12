/*
 * Copyright (c) 2018, Xyneex Technologies. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * You are not meant to edit or modify this source code unless you are
 * authorized to do so.
 *
 * Please contact Xyneex Technologies, #1 Orok Orok Street, Calabar, Nigeria.
 * or visit www.xyneex.com if you need additional information or have any
 * questions.
 */
package com.stockBroker.companyService.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockBroker.companyService.dto.AllStockResponse;
import com.stockBroker.companyService.model.StockBroker;
import com.stockBroker.companyService.repository.StockBrokerRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author BLAZE
 */
@Service
public class StockBrokerService
{
    private final StockBrokerRepository stockRepository;
    private final RestTemplate restTemplate;
    private final String iexcloudBaseUrl = "https://cloud.iexapis.com/stable";
    private final String iexcloudToken = "pk_41d531e4fdaa4dd8b75a2d0603937581";

    @Autowired
    public StockBrokerService(StockBrokerRepository stockRepository, RestTemplate restTemplate)
    {
        this.stockRepository = stockRepository;
        this.restTemplate = restTemplate;
    }

    public void updateStocks() throws IOException
    {
        String url = iexcloudBaseUrl + "/stock/market/list/mostactive?token=" + iexcloudToken;
        String json = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> stockData = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>()
        {
        });

        for(Map<String, Object> stock : stockData)
        {
            String companySymbol = (String)stock.get("symbol");
            String companyName = getCompanyName(companySymbol);
            String companyCurrency = (String)stock.get("currency");
            double companySharePrice = (double)stock.get("latestPrice");
            StockBroker s = stockRepository.findBycompanySymbol(companySymbol);
            s.setCompanyCurrency(companyCurrency);
            s.setCompanyName(companyName);
            s.setCompanySymbol(companySymbol);
            s.setCompanySharePrice(companySharePrice);
            s.setNoOfShares(s.getNoOfShares());
            stockRepository.save(s);
        }

    }

    public StockBroker updateStockQuantity(String symbol, double newQuantity)
    {
        StockBroker optionalStock = stockRepository.findBycompanySymbol(symbol);
        if(optionalStock != null)
        {
            StockBroker stock = optionalStock;
            stock.setNoOfShares(newQuantity);
            stockRepository.save(stock);
        }
        return optionalStock;
    }

    public String getCompanyName(String symbol) throws IOException
    {
        String url = iexcloudBaseUrl + "/stock/" + symbol + "/company?token=" + iexcloudToken;
        String json = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> companyData = objectMapper.readValue(json, new TypeReference<Map<String, Object>>()
        {
        });
        return (String)companyData.get("companyName");
    }

    public List<AllStockResponse> getAllAvailableStock()
    {
        List<StockBroker> stocks = stockRepository.findAll();
        return stocks.stream().map(stock -> mapToStockResponse(stock)).collect(Collectors.toList());
    }

    private AllStockResponse mapToStockResponse(StockBroker stockBroker)
    {
        AllStockResponse asr = new AllStockResponse();
        asr.setCompanyCurrency(stockBroker.getCompanyCurrency());
        asr.setCompanyName(stockBroker.getCompanyName());
        asr.setNoOfShares(stockBroker.getNoOfShares());
        asr.setCompanySymbol(stockBroker.getCompanySymbol());
        return asr;
    }
}
