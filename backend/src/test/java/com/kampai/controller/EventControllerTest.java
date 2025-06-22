package com.kampai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kampai.entity.Event;
import com.kampai.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Event sampleEvent;

    @BeforeEach
    void setup() {
        // テスト用のEventオブジェクトを作成
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setTitle("Sample Title");
        sampleEvent.setDate(LocalDate.now().plusDays(1));
        sampleEvent.setLocation("Sample Location");
        sampleEvent.setMaxParticipants(10);
        sampleEvent.setCreatedBy(1L);
    }

    @Test
    void testGetAllEvents() throws Exception {
        // 全イベント取得のテスト（成功ケース）
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(sampleEvent));

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))  // イベントが1件返ることを確認
            .andExpect(jsonPath("$[0].title", is("Sample Title"))); // タイトルが一致することを確認
    }

    @Test
    void testGetEventById_found() throws Exception {
        // 指定IDのイベント取得（存在する場合）
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        mockMvc.perform(get("/api/events/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.location", is("Sample Location"))); // 場所が一致することを確認
    }

    @Test
    void testGetEventById_notFound() throws Exception {
        // 指定IDのイベント取得（存在しない場合→404）
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEvent_valid() throws Exception {
        // イベント作成（正常データによる成功ケース）
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleEvent)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Sample Title"))); // 作成されたイベントのタイトルを確認
    }

    @Test
    void testCreateEvent_invalid_missingTitle() throws Exception {
        // イベント作成（タイトルが空でバリデーションエラーとなるケース）
        sampleEvent.setTitle("");  // 空文字に設定

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleEvent)))
            .andExpect(status().isBadRequest()); // バリデーションエラーで400を期待
    }

    @Test
    void testUpdateEvent_found() throws Exception {
        // イベント更新（存在するイベントを正常に更新するケース）
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Title");
        updatedEvent.setDate(LocalDate.now().plusDays(2));
        updatedEvent.setLocation("Updated Location");
        updatedEvent.setMaxParticipants(20);
        updatedEvent.setCreatedBy(2L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        mockMvc.perform(put("/api/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEvent)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Updated Title")))
            .andExpect(jsonPath("$.maxParticipants", is(20)));
    }

    @Test
    void testUpdateEvent_notFound() throws Exception {
        // イベント更新（対象IDが存在しないため404となるケース）
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleEvent)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEvent_found() throws Exception {
        // イベント削除（存在するIDの削除成功ケース）
        when(eventRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(eventRepository).deleteById(1L);

        mockMvc.perform(delete("/api/events/1"))
            .andExpect(status().isNoContent()); // 削除成功で204を期待
    }

    @Test
    void testDeleteEvent_notFound() throws Exception {
        // イベント削除（存在しないIDのため404となるケース）
        when(eventRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/events/1"))
            .andExpect(status().isNotFound());
    }
}
