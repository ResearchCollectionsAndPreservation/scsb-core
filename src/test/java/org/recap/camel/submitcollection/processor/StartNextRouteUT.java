package org.recap.camel.submitcollection.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.EndpointHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.springframework.test.util.ReflectionTestUtils;

public class StartNextRouteUT extends BaseTestCase {
    StartNextRoute startNextRoute;

    @InjectMocks
    StartNextRoute mockedStartNextRoute;
    @Mock
    private ProducerTemplate producer;

    @Mock
    CamelContext camelContext;
    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(mockedStartNextRoute, "emailToNYPL","testNypl@gmail.com" );
        ReflectionTestUtils.setField(mockedStartNextRoute, "emailToCUL","testCul@gmail.com" );
        ReflectionTestUtils.setField(mockedStartNextRoute, "emailToPUL","testPul@gmail.com" );
        MockitoAnnotations.initMocks(this);
    }
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
       // mockedStartNextRoute.sendEmailForEmptyDirectory(ex);
    }
}
