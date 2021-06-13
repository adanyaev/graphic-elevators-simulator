package com.company;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.application.Platform;

enum elevator_status{
    WAITING("WAITING"),
    MOVING_UP("MOVING_UP"),
    MOVING_DOWN("MOVING_DOWN");
    private String str;
    elevator_status(String str){this.str = str;}
    String getString(){return str;}
}

enum direction{
    UP("UP"),
    DOWN("DOWN");
    private String str;
    direction(String str){this.str = str;}
    String getString(){return str;}
}


class Request{
    direction dir;
    int start;
    int target;
    int id;
    Request(direction d, int start, int id){
        this.dir = d;
        this.start = start;
        this.id = id;
    }
}

public class Main extends Application {
    static int requests_frequency = 2; // заявки генерируются рандомно один раз в от 1 до requests_frequency секунд
    static int num_floors = 5;
    static int num_elevators = 3;
    static int seconds_between_floors = 2;
    static int max_persons = 4;
    static int lift_width = 70;
    static int lift_height = 100;
    static int space_between_lifts = 20;
    static int window_width = num_elevators*lift_width + space_between_lifts*(num_elevators+1);
    static int window_height = num_floors*lift_height;
    static ArrayList<Elevator> elevators;
    static ArrayList <Rectangle> lifts;
    static ArrayList <Text> texts;
    static Scene scene;

    public static void cls() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception E) {
            System.out.println(E);
        }
    }

    static void consoleWriter(ArrayList<Elevator> elevators) {
        try {
            while (true) {
                cls();
                for (int i = 0; i < elevators.size(); i++) {
                    System.out.println("Elevator # " + (i + 1));
                    System.out.println("Elevator status: " + elevators.get(i).current_status.getString());
                    System.out.println("Current floor: " + elevators.get(i).current_floor + '\n');
                    System.out.println("Waiting persons: ");
                    for (int j = 0; j < elevators.get(i).requestsWaiting.size(); j++) {
                        System.out.println("Person id: " + elevators.get(i).requestsWaiting.get(j).id +
                                " Start: " + elevators.get(i).requestsWaiting.get(j).start +
                                " Target: " + elevators.get(i).requestsWaiting.get(j).target + "   ");
                    }
                    System.out.println("Running persons: ");
                    for (int j = 0; j < elevators.get(i).requestsRunning.size(); j++) {
                        System.out.println("Person id: " + elevators.get(i).requestsRunning.get(j).id +
                                " Start: " + elevators.get(i).requestsRunning.get(j).start +
                                " Target: " + elevators.get(i).requestsRunning.get(j).target + "   ");
                    }
                    System.out.println("\n");
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void change_text(int id){
        Platform.runLater(new Runnable() {
            public void run() {
                texts.get(id).setText(String.valueOf(elevators.get(id).requestsRunning.size()));
            }
        });
    }

    public static void create_animation(int id){
        Platform.runLater(new Runnable() {
            public void run() {
                int offset = elevators.get(id).current_status == elevator_status.MOVING_UP ? -lift_height : lift_height;
                TranslateTransition t = new TranslateTransition();
                t.setDuration(Duration.seconds(seconds_between_floors));
                t.setByY(offset);
                t.setNode(lifts.get(id));
                TranslateTransition t1 = new TranslateTransition();
                t1.setDuration(Duration.seconds(seconds_between_floors));
                t1.setByY(offset);
                t1.setNode(texts.get(id));
                t.play();
                t1.play();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception{
        stage.setScene(scene);
        stage.setTitle("Elevators simulator");
        stage.setWidth(window_width + 50);
        stage.setHeight(window_height + 50);
        stage.show();
    }

    static void create_window(){
        lifts = new ArrayList <Rectangle> ();
        texts = new ArrayList <Text> ();
        Group group = new Group();
        for (int i = 0; i < num_elevators; i++){
            lifts.add(new Rectangle(space_between_lifts*(i+1) + i*lift_width, window_height - lift_height, lift_width, lift_height));
            lifts.get(i).setFill(Color.WHITE);
            lifts.get(i).setStroke(Color.BLACK);
            group.getChildren().add(lifts.get(i));
        }
        for (int i = 0; i < num_elevators; i++){
            texts.add(new Text(lifts.get(i).getX()+lift_width/2-5, lifts.get(i).getY()+lift_height/2, "0"));
            group.getChildren().add(texts.get(i));
        }
        scene = new Scene(group);
    }

    public static void main(String[] args) {
        elevators = new ArrayList<Elevator> ();
	    for (int i = 0; i < num_elevators; i++){
	        elevators.add(new Elevator(i));
        }
	    create_window();
	    Thread [] threads = new Thread[num_elevators];
        for (int i = 0; i < num_elevators; i++){
            threads[i] = new Thread(elevators.get(i));
            threads[i].start();
        }
        ElevatorsManager manager = new ElevatorsManager(elevators);
        Thread managerThread = new Thread(manager);
        managerThread.start();
        //consoleWriter(elevators);
        launch(args);

    }
}
