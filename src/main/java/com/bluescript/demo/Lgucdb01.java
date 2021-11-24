package com.bluescript.demo;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.swagger.annotations.ApiResponses;
import com.bluescript.demo.jpa.IUpdateCustomerJpa;
import com.bluescript.demo.model.WsHeader;
import com.bluescript.demo.model.ErrorMsg;
import com.bluescript.demo.model.EmVariable;
import com.bluescript.demo.model.CaCustomerRequest;
import com.bluescript.demo.model.Dfhcommarea;

@Getter
@Setter
@RequiredArgsConstructor
@Log4j2
@Component

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @io.swagger.annotations.ApiResponse(code = 400, message = "This is a bad request, please follow the API documentation for the proper request format"),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Due to security constraints, your access request cannot be authorized"),
        @io.swagger.annotations.ApiResponse(code = 500, message = "The server/Application is down. Please contact support team.") })

public class Lgucdb01 {

    @Autowired
    private WsHeader wsHeader;
    @Autowired
    private ErrorMsg errorMsg;
    @Autowired
    private EmVariable emVariable;
    @Autowired
    private Dfhcommarea dfhcommarea;
    private int db2CustomernumInt;
    private String wsTime;
    private String wsDate;
    private String caData;
    @Autowired
    private IUpdateCustomerJpa updateCustomerJpa;
    private int eibcalen;
    @Value("${api.LGUCVS01.uri}")
    private String LGUCVS01_URI;
    @Value("${api.LGUCVS01.host}")
    private String LGUCVS01_HOST;
    @Value("${api.LGSTSQ.host}")
    private String LGSTSQ_HOST;
    @Value("${api.LGSTSQ.uri}")
    private String LGSTSQ_URI;
    private String caErrorMsg;
    private String wsAbstime;

    @PostMapping("/lgucdb01")
    public void main(@RequestBody Dfhcommarea payload) {
        // if( eibcalen == 0 )
        // {
        // errorMsg.setEmVariable(" NO COMMAREA RECEIVED"); writeErrorMessage();
        // log.error(Error code : LGCA)
        // throw new LGCAException("LGCA");

        // }
        BeanUtils.copyProperties(payload, dfhcommarea);
        db2CustomernumInt = (int) dfhcommarea.getCaCustomerNum();
        updateCustomerInfo();
        try {
            WebClient webclientBuilder = WebClient.create(LGUCVS01_HOST);
            Mono<Dfhcommarea> lgucvs01Resp = webclientBuilder.post().uri(LGUCVS01_URI)
                    .body(Mono.just(dfhcommarea), Dfhcommarea.class).retrieve().bodyToMono(Dfhcommarea.class)
                    .timeout(Duration.ofMillis(10_000));
            dfhcommarea = lgucvs01Resp.block();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Transactional // Set isolation if required
    public void updateCustomerInfo() {
        log.debug("MethodupdateCustomerInfostarted..");
        emVariable.setEmSqlreq(" UPDATE CUST");
        try {
            updateCustomerJpa.updateCustomerByCaFirstNameAndCaLastNameAndCaDob(
                    dfhcommarea.getCaCustomerRequest().getCaFirstName(),
                    dfhcommarea.getCaCustomerRequest().getCaLastName(), dfhcommarea.getCaCustomerRequest().getCaDob(),
                    dfhcommarea.getCaCustomerRequest().getCaHouseName(),
                    dfhcommarea.getCaCustomerRequest().getCaHouseNum(),
                    dfhcommarea.getCaCustomerRequest().getCaPostcode(),
                    dfhcommarea.getCaCustomerRequest().getCaPhoneHome(),
                    dfhcommarea.getCaCustomerRequest().getCaPhoneMobile(),
                    dfhcommarea.getCaCustomerRequest().getCaEmailAddress(), db2CustomernumInt);

        } catch (Exception e) {
            log.error(e);
        }

        log.debug("Method updateCustomerInfo completed..");
    }

    public void writeErrorMessage() {
        log.debug("MethodwriteErrorMessagestarted..");
        wsAbstime = LocalTime.now().toString();
        wsAbstime = LocalTime.now().toString();
        wsDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMDDYYYY"));
        wsTime = LocalTime.now().toString();
        errorMsg.setEmDate(wsDate.substring(0, 8));
        errorMsg.setEmTime(wsTime.substring(0, 6));
        WebClient webclientBuilder = WebClient.create(LGSTSQ_HOST);
        try {
            Mono<ErrorMsg> lgstsqResp = webclientBuilder.post().uri(LGSTSQ_URI)
                    .body(Mono.just(errorMsg), ErrorMsg.class).retrieve().bodyToMono(ErrorMsg.class)
                    .timeout(Duration.ofMillis(10_000));
            errorMsg = lgstsqResp.block();
        } catch (Exception e) {
            log.error(e);
        }
        if (eibcalen > 0) {
            if (eibcalen < 91) {
                try {
                    Mono<ErrorMsg> lgstsqResp = webclientBuilder.post().uri(LGSTSQ_URI)
                            .body(Mono.just(errorMsg), ErrorMsg.class).retrieve().bodyToMono(ErrorMsg.class)
                            .timeout(Duration.ofMillis(10_000));
                    errorMsg = lgstsqResp.block();
                } catch (Exception e) {
                    log.error(e);
                }

            } else {
                try {
                    Mono<String> lgstsqResp = webclientBuilder.post().uri(LGSTSQ_URI)
                            .body(Mono.just(caErrorMsg), String.class).retrieve().bodyToMono(String.class)
                            .timeout(Duration.ofMillis(10_000));
                    caErrorMsg = lgstsqResp.block();
                } catch (Exception e) {
                    log.error(e);
                }

            }

        }

        log.debug("Method writeErrorMessage completed..");

    }

    /* End of program */
}