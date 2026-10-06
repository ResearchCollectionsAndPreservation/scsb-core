package org.recap.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.util.SecurityUtil;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class EncryptEmailAddressServiceTest {

    @InjectMocks
    private EncryptEmailAddressService encryptService;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Test
    public void testEncryptEmailAddress() {
        List<RequestItemEntity> requestItemEntityList = new ArrayList<>();
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setEmailId("test@example.com");
        requestItemEntityList.add(requestItemEntity);
        when(requestItemDetailsRepository.count()).thenReturn(1L);
        when(requestItemDetailsRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(requestItemEntityList));
        when(securityUtil.getEncryptedValue("test@example.com")).thenReturn("encryptedValue");
        String result = encryptService.encryptEmailAddress();
        assertTrue(result.startsWith("Total encrypted email Address - "));
        verify(requestItemDetailsRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testEncryptEmailAddressWithEmptyList() {
        when(requestItemDetailsRepository.count()).thenReturn(0L);
        String result = encryptService.encryptEmailAddress();
        verify(requestItemDetailsRepository, never()).saveAll(anyList());
    }

    @Test
    public void testEncryptEmailAddressWithException() {
        when(requestItemDetailsRepository.count()).thenReturn(1L);
        when(requestItemDetailsRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Test exception"));
        String result = encryptService.encryptEmailAddress();
        assertTrue(result.startsWith("Error occurred"));
        verify(requestItemDetailsRepository, never()).saveAll(anyList());
    }
}
