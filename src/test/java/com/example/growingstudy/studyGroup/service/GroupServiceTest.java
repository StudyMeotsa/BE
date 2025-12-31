package com.example.growingstudy.studyGroup.service;

import com.example.growingstudy.studyGroup.dto.GroupsListView;
import com.example.growingstudy.studyGroup.repository.GroupsRepository;
import com.example.growingstudy.studyGroup.service.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    GroupsRepository groupsRepository;

    @InjectMocks
    GroupService groupService;

    @Test
    void getGroupsList_callsRepositoryWithMemberIdAndNow_andReturnsResult() {
        // given
        Long memberId = 1L;

        // DTO를 직접 new 할 수 없으면 mock으로도 충분함
        GroupsListView v1 = mock(GroupsListView.class);
        GroupsListView v2 = mock(GroupsListView.class);
        List<GroupsListView> expected = List.of(v1, v2);

        when(groupsRepository.findGroupsByMember(eq(memberId), any(LocalDateTime.class)))
                .thenReturn(expected);

        // when
        List<GroupsListView> result = groupService.getGroupsList(memberId);

        // then (반환값 검증)
        assertThat(result).isSameAs(expected);

        // then (레포 호출 검증 + now 캡쳐)
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(groupsRepository, times(1)).findGroupsByMember(eq(memberId), timeCaptor.capture());

        LocalDateTime capturedNow = timeCaptor.getValue();
        assertThat(capturedNow).isNotNull();
        // 필요하면 "너무 과거/미래가 아닌지" 정도만 체크 가능
    }
}