package models;

/**
 * Created by ramakrk on 7/25/2017.
 */
public class Coordinate
{
    double latitude;
    double longitude;
    boolean isValid;

    // Getters and setters

    public boolean isValid()
    {
        return isValid;
    }

    public void setValid(boolean valid)
    {
        isValid = valid;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public Coordinate(double Latitude, double Longitude)
    {
        this.latitude = Latitude;
        this.longitude = Longitude;

        this.isValid = true;
    }

}
