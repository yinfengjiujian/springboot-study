package com.neusoft.study.controller;

import com.neusoft.study.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: com.neusoft.study.controller</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/5/25 0025 7:38
 * Description: No Description
 */
@RestController
@RequestMapping("/ticket")

    public class TicketController {
    @Autowired
    private TicketService ticketService;

    @RequestMapping("/queryStock")
    public Object getTicketInfo(String ticketSeq){
        return ticketService.queryTicketStock(ticketSeq);
    }


}
