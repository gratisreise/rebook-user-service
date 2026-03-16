package com.example.rebookuserservice.domain.model.entity;

import com.example.rebookuserservice.domain.model.dto.request.UsersUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@ToString
public class Users {
  @Id
  @Column(length = 50)
  private String id;

  @Column(unique = true, length = 30)
  private String email;

  @Column(unique = true, length = 100)
  private String nickname;

  @Column(nullable = false, length = 300)
  private String profileImage;

  @Column(updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;

  public Users update(UsersUpdateRequest request) {
    this.nickname = request.nickname();
    this.email = request.email();
    return this;
  }
}
