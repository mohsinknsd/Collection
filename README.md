# CountinousTouchListener
It is simple to use this class, Just put following snippet in the code...

        new ContinuousTouchListener(8, view) {
            @Override
            public void onTouchesCompleted(View view, int touches) {
                openDialog("Please Enter Password", ACTION_NEXT);
            }
        };
