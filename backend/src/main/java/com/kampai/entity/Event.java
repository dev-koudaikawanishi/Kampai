package com.kampai.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "タイトルは必須です")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "日付は必須です")
    @FutureOrPresent(message = "日付は今日以降にしてください")
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank(message = "場所は必須です")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "最大参加人数は必須です")
    @Min(value = 1, message = "最大参加人数は1人以上である必要があります")
    @Column(nullable = false)
    private Integer maxParticipants;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdByUser;

    // getter/setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }
}
