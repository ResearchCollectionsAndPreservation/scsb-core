package org.recap.service.common;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RepositoryServiceUT extends BaseTestCaseUT {

    @InjectMocks
    RepositoryService repositoryService;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Test
    public void getItemStatusDetailsRepository() {
        ItemStatusDetailsRepository itemStatusDetailsRepository = repositoryService.getItemStatusDetailsRepository();
        assertNotNull(itemStatusDetailsRepository);
    }

    @Test
    public void getCollectionGroupDetailsRepository() {
        CollectionGroupDetailsRepository collectionGroupDetailsRepository = repositoryService.getCollectionGroupDetailsRepository();
        assertNotNull(collectionGroupDetailsRepository);
    }
}
