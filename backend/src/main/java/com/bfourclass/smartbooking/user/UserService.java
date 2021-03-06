package com.bfourclass.smartbooking.user;

import com.bfourclass.smartbooking.email.EmailService;
import com.bfourclass.smartbooking.hotel.HotelModel;
import com.bfourclass.smartbooking.hotel.HotelRepository;
import com.bfourclass.smartbooking.hotel.json.HotelJSON;
import com.bfourclass.smartbooking.requests.ResponseError;
import com.bfourclass.smartbooking.security.StringGenerator;
import com.bfourclass.smartbooking.user.auth.SecurityContext;
import com.bfourclass.smartbooking.user.json.OwnerRegisterJSONRequest;
import com.bfourclass.smartbooking.user.json.UserRegisterJSONRequest;
import com.bfourclass.smartbooking.user_history.UserHistoryModel;
import com.bfourclass.smartbooking.user_history.UserHistoryRepository;
import com.bfourclass.smartbooking.user_verification.UserVerification;
import com.bfourclass.smartbooking.user_verification.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserVerificationService userVerificationService;
    private final SecurityContext securityContext;
    private final HotelRepository hotelRepository;
    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService, UserVerificationService userVerificationService, SecurityContext securityContext, HotelRepository hotelRepository, UserHistoryRepository userHistoryRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userVerificationService = userVerificationService;
        this.securityContext = securityContext;
        this.hotelRepository = hotelRepository;
        this.userHistoryRepository = userHistoryRepository;
    }

    public boolean checkTokenIsValid(String token) {
        return securityContext.exists(token);
    }

    public UserModel getUserFromToken(String token) {
        String username = securityContext.extractUsername(token);
        return userRepository.findUserByUsername(username);
    }

    public void createUserByForm(UserRegisterJSONRequest registerForm) {
        UserModel userModel = registerForm.toUser();

        userRepository.save(userModel);
    }

    public void createOwnerByForm(OwnerRegisterJSONRequest registerForm) {
        UserModel userModel = registerForm.toUser();

        userRepository.save(userModel);
    }

    public boolean sendUserActivationEmail(UserModel userModel) {
        String activationToken = generateUserActivationToken(userModel);
        return emailService.sendEmailVerificationEmail(userModel.getUsername(), userModel.getEmail(), activationToken, "");
    }

    public String generateUserActivationToken(UserModel userModel) {
        String verificationKey = StringGenerator.generate();
        UserVerification userVerification = new UserVerification(userModel, verificationKey);

        userVerificationService.save(userVerification);

        return verificationKey;
    }

    public void makeAdmin(String username) {
        UserModel userModel = getUserByLogin(username);
        userModel.setAdmin(true);
        userRepository.save(userModel);
    }

    public void removeAdmin(String username) {
        UserModel userModel = getUserByLogin(username);
        userModel.setAdmin(false);
        userRepository.save(userModel);
    }

    public String loginUserReturnToken(UserModel user) {
        return securityContext.authenticateUserReturnToken(user);
    }

    public boolean userExists(String login) {
        return userRepository.findUserByUsername(login) != null || userRepository.findUserByEmail(login) != null;
    }

    public ResponseEntity<Object> checkUserToken(String token) {
        // check if token exists in request
        if (token == null) {
            return new ResponseEntity<>(new ResponseError("Missing Authorization header"), HttpStatus.UNAUTHORIZED);
        }

        // check if token exists in SecurityContext
        if (!checkTokenIsValid(token)) {
            return new ResponseEntity<>(new ResponseError("Invalid Authorization header"), HttpStatus.UNAUTHORIZED);
        }

        return null;
    }

    public UserModel getUserByLogin(String login) {
        UserModel userModel = userRepository.findUserByUsername(login);

        if (userModel != null) {
            return userModel;
        }

        return userRepository.findUserByEmail(login);
    }

    public List<HotelModel> getOwnerHotels(String ownerName) {
        UserModel userModel = userRepository.findUserByUsername(ownerName);
        List<HotelModel> response = null;
        if (userModel.isHotelOwner()) {
            response = hotelRepository.findByOwnerId(userModel.getId());
        }
        return response;
    }

    public List<HotelJSON> getUserHotels(UserModel userModel) {
        List<HotelJSON> response = new ArrayList<>();

        List<HotelModel> hotels = userModel.getUserHotels();

        for (HotelModel hotelModel : hotels) {
            response.add(new HotelJSON(hotelModel.getId(), hotelModel.getIdentifier(), hotelModel.getHotelUrl(), hotelModel.getHotelName(), hotelModel.getLocationName(), hotelModel.getAverageRating(), hotelModel.getVotes(),hotelModel.getPhotoLink(),hotelModel.getDescription(),hotelModel.getPrice()));
        }

        return response;
    }

    /* TODO, it's a little bit weird this logic*/
    public void addHotel(UserModel userModel, HotelModel hotelModel) {
        if (hotelRepository.findById(hotelModel.getId()).isEmpty()) {
            hotelRepository.save(hotelModel);
        }
        userModel.addHotel(hotelModel);
        userRepository.save(userModel);
    }

    public void deleteUserHotel(UserModel userModel, HotelModel hotelModel) {
        userModel.deleteUserHotel(hotelModel);
        userRepository.save(userModel);
    }


    public void addUserSearchHistory(UserModel userModel, String query) {
        UserHistoryModel userHistoryModel = new UserHistoryModel(query);
        userHistoryRepository.save(userHistoryModel);
        userModel.addHistory(userHistoryModel);
        userRepository.save(userModel);
    }

    public void removeSearchedHotel(UserModel userModel, long id) {
        userModel.removeSearchedHotelById(id);
        userRepository.save(userModel);

        UserHistoryModel userHistoryModel = userHistoryRepository.getOne(id);
        userHistoryRepository.delete(userHistoryModel);
    }


    public void sendEmailVerificationUpdate(UserModel userModel, String newEmail) {
        String activationToken = generateUserActivationToken(userModel);
        emailService.sendEmailVerificationEmail(userModel.getUsername(), newEmail, activationToken, "&new_email=" + newEmail);
    }

    public void updateUser(UserModel userModel) {
        userRepository.save(userModel);
    }

    public void saveUser(UserModel userModel) {
        userRepository.save(userModel);
    }

    public List<UserModel> getAll() {
        return userRepository.findAll();
    }
}
