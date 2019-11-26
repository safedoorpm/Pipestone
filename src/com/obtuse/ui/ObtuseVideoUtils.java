package com.obtuse.ui;

/**
 Created by danny on 2019/11/14.
 */

public class ObtuseVideoUtils {

//    public void mediaPlayer()
//    {
//
//        JFrame jf = new JFrame();
//        jf.setLayout(new BorderLayout());
//
//        //file you want to play
//        URL mediaURL = //Whatever
//                //create the media player with the media url
//                Player mediaPlayer = Manager.createRealizedPlayer(mediaURL);
//        //get components for video and playback controls
//        Component video = mediaPlayer.getVisualComponent();
//        Component controls = mediaPlayer.getControlPanelComponent();
//        add(video,BorderLayout.CENTER);
//        add(controls,BorderLayout.SOUTH);
//    }
//
//    private void getVideo(){
//        final JFXPanel VFXPanel = new JFXPanel();
//
//        File video_source = new File( "tutorial.mp4");
//        Media m = new Media( video_source.toURI().toString());
//        MediaPlayer player = new MediaPlayer(m);
//        MediaView viewer = new MediaView(player);
//
//        StackPane root = new StackPane();
//        Scene scene = new Scene(root);
//
//        // center video position
//        javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
//        viewer.setX((screen.getWidth() - videoPanel.getWidth()) / 2);
//        viewer.setY((screen.getHeight() - videoPanel.getHeight()) / 2);
//
//        // resize video based on screen size
//        DoubleProperty width = viewer.fitWidthProperty();
//        DoubleProperty height = viewer.fitHeightProperty();
//        width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));
//        height.bind( Bindings.selectDouble( viewer.sceneProperty(), "height"));
//        viewer.setPreserveRatio(true);
//
//        // add video to stackpane
//        root.getChildren().add(viewer);
//
//        VFXPanel.setScene(scene);
//        //player.play();
//        videoPanel.setLayout(new BorderLayout());
//        videoPanel.add( VFXPanel, BorderLayout.CENTER);
//    }

}
