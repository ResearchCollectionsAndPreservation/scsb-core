package org.recap.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;

import static org.junit.Assert.assertTrue;

public class ReportProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    ReportProcessor reportProcessor;

    @Mock
    Exchange exchange;

    @Mock
    Message message;

    @Mock
    ReportEntity reportEntity;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Test
    public void process() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getBody()).thenReturn(reportEntity);
        reportProcessor.process(exchange);
        assertTrue(true);
    }
}
