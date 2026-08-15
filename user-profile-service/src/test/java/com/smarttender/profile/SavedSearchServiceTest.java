package com.smarttender.profile;

import com.smarttender.profile.dto.request.SavedSearchRequest;
import com.smarttender.profile.dto.response.SavedSearchResponse;
import com.smarttender.profile.entity.SavedSearch;
import com.smarttender.profile.repository.SavedSearchRepository;
import com.smarttender.profile.service.impl.SavedSearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedSearchServiceTest {

    @Mock SavedSearchRepository repo;
    @InjectMocks SavedSearchServiceImpl service;

    @Test
    void create_saved_search_success() {
        SavedSearchRequest req = new SavedSearchRequest();
        req.setName("CPWD Civil Works");
        req.setKeywords("civil,construction");
        req.setOrganisation("CPWD");
        req.setAlertEnabled(true);
        req.setAlertFrequency("DAILY");

        SavedSearch saved = SavedSearch.builder().id(1L).userId(1L)
                .name("CPWD Civil Works").keywords("civil,construction")
                .alertEnabled(true).alertFrequency("DAILY").build();

        when(repo.save(any())).thenReturn(saved);

        SavedSearchResponse response = service.create(1L, req);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("CPWD Civil Works");
        assertThat(response.isAlertEnabled()).isTrue();
    }

    @Test
    void get_by_user_returns_list() {
        when(repo.findByUserId(1L)).thenReturn(List.of(
                SavedSearch.builder().id(1L).userId(1L).name("Search 1").alertEnabled(true).build(),
                SavedSearch.builder().id(2L).userId(1L).name("Search 2").alertEnabled(false).build()
        ));

        List<SavedSearchResponse> list = service.getByUser(1L);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo("Search 1");
    }
}
