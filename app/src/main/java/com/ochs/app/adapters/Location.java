package com.ochs.app.adapters;

public class Location
{
    private String locationName, headOffice, street, unitNo, city, countryName, zipcode, email, contactName, phoneno, active, isVirtualName,stateName;
    private int locationId, stateId, countryId, cmpId, locationCount;
    private Double longitude, latitude, pickLocChargePerMile;

    public void setActive(String active)
    {
        this.active = active;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStateId(int stateId)
    {
        this.stateId = stateId;
    }

    public void setStateName(String stateName)
    {
        this.stateName = stateName;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public void setCmpId(int cmpId) {
        this.cmpId = cmpId;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHeadOffice(String headOffice) {
        this.headOffice = headOffice;
    }

    public void setIsVirtualName(String isVirtualName) {
        this.isVirtualName = isVirtualName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setPickLocChargePerMile(Double pickLocChargePerMile) {
        this.pickLocChargePerMile = pickLocChargePerMile;
    }

    public String getEmail() {
        return email;
    }

    public String getActive() {
        return active;
    }

    public String getCity() {
        return city;
    }

    public String getContactName() {
        return contactName;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public int getCmpId() {
        return cmpId;
    }

    public int getCountryId() {
        return countryId;
    }

    public int getLocationCount() {
        return locationCount;
    }

    public int getLocationId() {
        return locationId;
    }

    public int getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getHeadOffice() {
        return headOffice;
    }

    public String getIsVirtualName() {
        return isVirtualName;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getStreet() {
        return street;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public String getZipcode() {
        return zipcode;
    }

    public Double getPickLocChargePerMile() {
        return pickLocChargePerMile;
    }
}
