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
package com.stockBroker.companyService.repository;

import com.stockBroker.companyService.model.StockBroker;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author BLAZE
 */
public interface StockBrokerRepository extends JpaRepository<StockBroker, Long>
{
//    Optional<StockBroker> findBycompanySymbol(String symbol);

    StockBroker findBycompanySymbol(String companySymbol);
}
