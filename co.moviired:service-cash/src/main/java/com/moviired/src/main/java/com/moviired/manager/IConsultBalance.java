package com.moviired.manager;

import com.moviired.client.balance.Response;
import com.moviired.model.request.CashOutRequest;


public interface IConsultBalance {

    Response consultBalance(CashOutRequest request);

}

