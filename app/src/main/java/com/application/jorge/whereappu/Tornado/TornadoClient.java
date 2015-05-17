package com.application.jorge.whereappu.Tornado;

import com.application.jorge.whereappu.Activities.App;

/**
 * Created by Jorge on 17/05/2015.
 */
public class TornadoClient {
    public static class TestClass {
        public static void sendInfo( int clientID){
            App.softAlert(clientID);
        }
    }
}
