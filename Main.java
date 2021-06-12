package com.company;

import java.io.IOException;
import java.util.ArrayList;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.application.Application;

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
    static ArrayList<Elevator> elevators;
    static int requests_frequency = 8; // заявки генерируются рандомно один раз в от 1 до requests_frequency секунд
    static int num_floors = 7;
    static int num_elevators = 3;
    static int seconds_between_floors = 2;
    static int max_persons = 4;

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

    @Override
    public void start(Stage stage) throws Exception{
        ArrayList <Rectangle> lifts = new ArrayList <Rectangle> ();
        int lift_width = 70;
        int lift_height = 100;
        int space_between_lifts = 20;
        int window_width = num_elevators*lift_width + space_between_lifts*(num_elevators+1);
        int window_height = num_floors*lift_height;
        Group group = new Group();
        for (int i = 0; i < num_elevators; i++){
            lifts.add(new Rectangle(space_between_lifts*(i+1) + i*lift_width, window_height - lift_height, lift_width, lift_height));
            lifts.get(i).setFill(Color.WHITE);
            lifts.get(i).setStroke(Color.BLACK);
            group.getChildren().add(lifts.get(i));
        }
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setTitle("First Application");
        stage.setWidth(window_width + 50);
        stage.setHeight(window_height + 50);
        stage.show();

    }

    public static void main(String[] args) {
        elevators = new ArrayList<Elevator> ();
	    for (int i = 0; i < num_elevators; i++){
	        elevators.add(new Elevator());
        }
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
