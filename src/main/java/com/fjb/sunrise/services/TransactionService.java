package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.StatisticResponse;
import com.fjb.sunrise.models.Transaction;
import java.text.ParseException;
import org.springframework.data.domain.Page;

public interface TransactionService {
    Transaction create(CreateOrUpdateTransactionRequest request) throws ParseException;

    Page<Transaction> getTransactionList(DataTableInputDTO payload, String email);

    Transaction update(CreateOrUpdateTransactionRequest request) throws ParseException;

    StatisticResponse statistic();
}
