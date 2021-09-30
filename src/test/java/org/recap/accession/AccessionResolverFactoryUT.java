package org.recap.accession;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.service.accession.AccessionResolverAbstract;
import org.recap.service.accession.AccessionResolverFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class AccessionResolverFactoryUT extends BaseTestCaseUT {

    @InjectMocks
    AccessionResolverFactory accessionResolverFactory;

    @Test
    public void getFormatResolver(){
        List<AccessionResolverAbstract> accessionResolverAbstractList = new ArrayList<>();
        ReflectionTestUtils.setField(accessionResolverFactory,"accessionResolverAbstractList",accessionResolverAbstractList);
        try {
            accessionResolverFactory.getFormatResolver("test");
        }catch (Exception e){}
    }
}
