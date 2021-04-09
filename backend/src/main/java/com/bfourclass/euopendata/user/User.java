package com.bfourclass.euopendata.user;

import com.bfourclass.euopendata.location.Location;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String displayName;
    private String profilePhotoLink;
    private boolean isActivated = false;

    @ManyToMany
    private Set<Location> locations;

    public User(String username, String email, String password, String displayName, String profilePhotoLink) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.locations = null;
        this.profilePhotoLink = profilePhotoLink;
    }

    public User() { }

    public Long getUserId() {
        return id;
    }

    public void setUserId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePhotoLink() {
        return profilePhotoLink;
    }

    public void setProfilePhotoLink(String profilePhotoLink) {
        this.profilePhotoLink = profilePhotoLink;
    }

    public boolean existingLocation(String locationName) {
        if (this.locations.isEmpty())
            return false;
        for (Location location : this.locations) {
            if (location.getLocationName().equals(locationName))
                return true;
        }
        return false;
    }

    public void addLocationToFavourites(String locationName) {
        locations.add(new Location(locationName));
    }

    public void deleteLocationFromFavourites(String locationName) {
        this.locations.removeIf(location -> location.getLocationName().equals(locationName));
    }

    public Set<Location> getLocations() {
        return locations;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profilePhotoLink='" + profilePhotoLink + '\'' +
                '}';
    }

    public void activateUser() {
        isActivated = true;
    }
}
