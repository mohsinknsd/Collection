# CountinousTouchListener
It is simple to use this class, Just put following snippet in the code...

        //This will be activated on 8 countinous touches
        new ContinuousTouchListener(8, view) {
            @Override
            public void onTouchesCompleted(View view, int touches) {
                //Do your stuff here...
            }
        };
