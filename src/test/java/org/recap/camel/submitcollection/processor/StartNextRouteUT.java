package org.recap.camel.submitcollection.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.recap.BaseTestCaseUT;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;


public class StartNextRouteUT extends BaseTestCaseUT {
    StartNextRoute startNextRoute;

    @InjectMocks
    StartNextRoute mockedStartNextRoute;

    @Mock
    private ProducerTemplate producer;

    @Mock
    CamelContext camelContext;


    @Test
    public void testStartNextRoute() {
        startNextRoute = new StartNextRoute("pulSubmitCollectionFTPCgdProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }

    @Test
    public void testStartNextRouteForPulSubmitCollectionFTPCgdNotProtectedRoute() {
        startNextRoute = new StartNextRoute("pulSubmitCollectionFTPCgdNotProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }
    @Test
    public void testStartNextRouteForCulSubmitCollectionFTPCgdProtectedRoute() {
        startNextRoute = new StartNextRoute("culSubmitCollectionFTPCgdProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }
    @Test
    public void testStartNextRouteForCulSubmitCollectionFTPCgdNotProtectedRoute() {
        startNextRoute = new StartNextRoute("culSubmitCollectionFTPCgdNotProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }
    @Test
    public void testStartNextRouteForNyplSubmitCollectionFTPCgdProtectedRoute() {
        startNextRoute = new StartNextRoute("nyplSubmitCollectionFTPCgdProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }
    @Test
    public void testStartNextRouteForNyplSubmitCollectionFTPCgdNotProtectedRoute() {
        startNextRoute = new StartNextRoute("nyplSubmitCollectionFTPCgdNotProtectedRoute");

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");

        try {
            startNextRoute.process(ex);} catch (Exception e) {}
        try{
            startNextRoute.sendEmailForEmptyDirectory(ex);} catch (Exception e) {}
    }

    @Test
    public void sendEmailForEmptyDirectoryForPulSubmitCollectionFTPCgdProtectedRoute() throws Exception {
        ReflectionTestUtils.setField(mockedStartNextRoute, "routeId","pulSubmitCollectionFTPCgdProtectedRoute" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setBody("Test text for Example");
    }
}
