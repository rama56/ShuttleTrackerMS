package models;

import java.util.Date;

/**
 * Created by ramakrk on 7/25/2017.
 */
public class LocationData
{
    private Date time;
    private Coordinate point;
    private String busRoute;    //TODO: Make bus routes as enums instead of strings.

    private boolean isValid;

    public  LocationData()
    {
        this.isValid = false;
    }

    public LocationData(Coordinate Point, String BusRoute, Date time)
    {
        this.point = Point;
        this.busRoute = BusRoute;
        this.time= time;

        // Add more validations later.
        this.isValid = true;
    }

    // Getters and setters

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Coordinate getPoint()
    {
        return point;
    }

    public void setPoint(Coordinate point)
    {
        this.point = point;
    }

    public String getBusRoute()
    {
        return busRoute;
    }

    public void setBusRoute(String busRoute)
    {
        this.busRoute = busRoute;
    }

    public boolean isValid()
    {
        return isValid;
    }

    public void setValid(boolean valid)
    {
        isValid = valid;
    }

}
