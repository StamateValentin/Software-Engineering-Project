package com.bfourclass.smartbooking.hotel;

import com.bfourclass.smartbooking.hotel.json.HotelJSON;
import com.bfourclass.smartbooking.hotel_review.HotelReviewModel;
import com.bfourclass.smartbooking.hotel_review.HotelReviewRepository;
import com.bfourclass.smartbooking.hotel_review.json.HotelReviewJSON;
import com.bfourclass.smartbooking.user.UserModel;
import com.bfourclass.smartbooking.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final HotelReviewRepository hotelReviewRepository;

    @Autowired
    HotelService(UserRepository userRepository, HotelRepository hotelRepository, HotelReviewRepository hotelReviewRepository) {
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.hotelReviewRepository = hotelReviewRepository;
    }

    public void save(HotelModel hotelModel) {
        hotelRepository.save(hotelModel);
    }

    public void delete(HotelModel hotelModel){hotelRepository.delete(hotelModel);}

    public void createHotelIfNotExists(HotelModel hotelModel) {
        if (!locationExists(hotelModel.getIdentifier())) {
            hotelRepository.save(hotelModel);
            System.out.println("Not exist");
        }
    }

    private boolean locationExists(String identifier) {
        return hotelRepository.findByIdentifier(identifier) != null;
    }

    public List<HotelJSON> getHotels() {
        List<HotelModel> hotelModels = hotelRepository.getAll();
        List<HotelJSON> hotelJSONList = new ArrayList<>();

        for (HotelModel hotelModel : hotelModels) {
            hotelJSONList.add(new HotelJSON(hotelModel.getId(), hotelModel.getIdentifier(), hotelModel.getHotelUrl(), hotelModel.getHotelName(), hotelModel.getLocationName(), hotelModel.getAverageRating(), hotelModel.getVotes(),hotelModel.getPhotoLink(),hotelModel.getDescription(),hotelModel.getPrice()));
        }

        return hotelJSONList;
    }

    public boolean addReview(UserModel userModel, HotelModel hotelModel, String message, int rating) {

        if (message.length() < 10 || rating == 0) {
            return false;
        }

        if (userModel.hasAlreadyReviewedHotel(hotelModel)) {
            return false;
        }

        HotelReviewModel hotelReviewModel = new HotelReviewModel(userModel, hotelModel, message, rating);

        hotelReviewRepository.save(hotelReviewModel);
        userRepository.save(userModel);
        hotelRepository.save(hotelModel);
        return true;
    }

    public boolean removeReview(UserModel userModel, HotelModel hotelModel, HotelReviewModel hotelReviewModel) {
        userModel.removeHotelReview(hotelReviewModel);
        hotelModel.removeHotelReview(hotelReviewModel);

        hotelReviewRepository.delete(hotelReviewModel);

        userRepository.save(userModel);
        hotelRepository.save(hotelModel);
        return true;
    }

    public void updateHotelReview(HotelModel hotelModel, HotelReviewModel oldReviewModel, HotelReviewJSON request) {
        hotelModel.updateHotelReviewNumber(oldReviewModel.getRating(), true);
        hotelModel.updateHotelReviewNumber(request.userRating, false);

        oldReviewModel.setReviewMessage(request.reviewMessage);
        oldReviewModel.setRating(request.userRating);

        hotelReviewRepository.save(oldReviewModel);
        hotelRepository.save(hotelModel);
    }

    public HotelModel getHotelById(long id) {
        if (hotelRepository.findById(id).isPresent()) {
            return hotelRepository.findById(id).get();
        }

        return null;
    }

    public HotelModel getHotelByIdentifier(String identifier) {
        return hotelRepository.findByIdentifier(identifier);
    }
}
