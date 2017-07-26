package com.example.ramakrk.shuttletrackerms;

/**
 * Created by ramakrk on 7/24/2017.
 */
public class ServiceInteractorHelperFactory
{
    public ServiceInteractorBase getBackendHelper(String dataMode)
    {
        if (dataMode.equals("Real"))
        {
            return new ServiceInteractorReal();
        }
        else if (dataMode.equals("Mock"))
        {
            return new ServiceInteractorMock();
        }
        else
        {
            return null;
            //throw new Exception("Code should not reach here theoretically");
        }
    }
}
