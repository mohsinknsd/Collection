# CountinousTouchListener
It is simple to use this class, Just put following snippet in the code...

        //This will be activated on 8 countinous touches
        new ContinuousTouchListener(8, view) {
            @Override
            public void onTouchesCompleted(View view, int touches) {
                //Do your stuff here...
            }
        };
# Logger
A logger to records logs on the console or file. It can be used using different levels of logging as following...
        //it will pick file.not.found property from messages.properties file
        Log.e(getClass(), "file.not.found", "resources/hardcoded.json");
