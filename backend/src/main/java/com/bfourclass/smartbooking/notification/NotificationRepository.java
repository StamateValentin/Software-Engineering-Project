package com.bfourclass.smartbooking.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, Long> {
    @Query("SELECT n FROM NotificationModel n WHERE n.id = ?1")
    NotificationModel findById(String id);
}
