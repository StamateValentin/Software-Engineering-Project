package com.bfourclass.smartbooking.hotel_review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HotelReviewRepository extends JpaRepository<HotelReviewModel, Long> {
}
